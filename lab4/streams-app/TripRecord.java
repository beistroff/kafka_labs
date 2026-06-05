package com.lab4;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TripRecord {
    @JsonProperty("trip_date") public String tripDate;
    @JsonProperty("duration") public int duration;
    @JsonProperty("start_station") public String startStation;
    @JsonProperty("end_station") public String endStation;
}
