package com.lab4;

import java.util.*;

public class DailyAggregate {
    public String date;
    public long count = 0;
    public double totalDuration = 0;
    public Map<String, Long> startStations = new HashMap<>();
    public Map<String, Long> allStations = new HashMap<>();

    public DailyAggregate update(TripRecord trip) {
        this.date = trip.tripDate;
        this.count++;
        this.totalDuration += trip.duration;

        // Рахуємо старти
        startStations.put(trip.startStation, startStations.getOrDefault(trip.startStation, 0L) + 1);

        // Рахуємо всі відвідування (старт + фініш)
        allStations.put(trip.startStation, allStations.getOrDefault(trip.startStation, 0L) + 1);
        allStations.put(trip.endStation, allStations.getOrDefault(trip.endStation, 0L) + 1);

        return this;
    }

    public String getAvgDuration() {
        return String.format(Locale.US, "%.2f", count == 0 ? 0 : totalDuration / count);
    }

    public String getMostPopularStart() {
        return startStations.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("Unknown");
    }

    public String getTop3Stations() {
        List<Map.Entry<String, Long>> sorted = new ArrayList<>(allStations.entrySet());
        sorted.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        List<String> top3 = sorted.stream().limit(3).map(Map.Entry::getKey).toList();
        return String.join(", ", top3);
    }
}