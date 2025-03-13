package io.snowtracker.weather.weather_stream.models;

import java.time.ZonedDateTime;

import lombok.Data;

/**
 * Represents a single day's forecast data
 */
@Data
public class DailyForecast {
    private String periodName;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private int temperature;
    private String temperatureUnit;
    private String shortForecast;
    private String detailedForecast;
    private double snowfall; // Snow accumulation in inches
} 