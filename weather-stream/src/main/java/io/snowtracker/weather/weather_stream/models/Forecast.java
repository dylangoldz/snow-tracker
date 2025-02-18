package io.snowtracker.weather.weather_stream.models;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class Forecast {
    private String periodName;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private int temperature;
    private String temperatureUnit;
    private String shortForecast;
}
