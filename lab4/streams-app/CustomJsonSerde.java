package com.lab4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public class CustomJsonSerde<T> implements Serde<T> {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<T> type;

    public CustomJsonSerde(Class<T> type) { this.type = type; }

    @Override
    public Serializer<T> serializer() {
        return (topic, data) -> {
            try { return mapper.writeValueAsBytes(data); } 
            catch (Exception e) { return new byte[0]; }
        };
    }

    @Override
    public Deserializer<T> deserializer() {
        return (topic, data) -> {
            if (data == null || data.length == 0) return null;
            try { return mapper.readValue(data, type); } 
            catch (IOException e) { return null; }
        };
    }
    @Override public void configure(java.util.Map<String, ?> configs, boolean isKey) {}
    @Override public void close() {}
}