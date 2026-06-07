package com.lab4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

import java.util.Properties;

public class KafkaStreamsApp {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-lab4-analysis");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9093");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);

        ObjectMapper mapper = new ObjectMapper();
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> sourceStream = builder.stream("trips-input");

        KTable<String, DailyAggregate> aggregatedTable = sourceStream
            .map((k, v) -> {
                try {
                    TripRecord record = mapper.readValue(v, TripRecord.class);
                    return new KeyValue<>(record.tripDate, record);
                } catch (Exception e) {
                    return new KeyValue<>(null, null);
                }
            })
            .filter((k, v) -> k != null)
            .groupByKey(Grouped.with(Serdes.String(), new CustomJsonSerde<>(TripRecord.class)))
            .aggregate(
                DailyAggregate::new,
                (key, value, aggregate) -> aggregate.update(value),
                Materialized.with(Serdes.String(), new CustomJsonSerde<>(DailyAggregate.class))
            );

        // Розбиваємо результати на 4 окремі топіки згідно з вимогами
        KStream<String, DailyAggregate> resultStream = aggregatedTable.toStream()
            .filter((k, v) -> k != null && v != null);

        // a. Середня тривалість
        resultStream.mapValues(DailyAggregate::getAvgDuration).to("topic-avg-duration", Produced.with(Serdes.String(), Serdes.String()));
        // b. Кількість поїздок
        resultStream.mapValues(agg -> String.valueOf(agg.count)).to("topic-trips-count", Produced.with(Serdes.String(), Serdes.String()));
        // c. Найпопулярніша початкова станція (старт + фініш)
        resultStream.mapValues(DailyAggregate::getMostPopularStation).to("topic-popular-start", Produced.with(Serdes.String(), Serdes.String()));
        // d. Трійка лідерів станцій
        resultStream.mapValues(DailyAggregate::getTop3Stations).to("topic-top3-stations", Produced.with(Serdes.String(), Serdes.String()));

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}