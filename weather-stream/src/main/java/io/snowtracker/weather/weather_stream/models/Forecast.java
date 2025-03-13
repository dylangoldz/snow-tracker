package io.snowtracker.weather.weather_stream.models;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;

@Data
public class Forecast {
    // Overall forecast period
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    
    // Total snowfall across all periods
    private double totalSnowfall;
    
    // Daily forecasts
    private List<DailyForecast> dailyForecasts;
    
    // Resort information
    private String resortId;
    private String resortName;
    private String regionId;
    
    // Metadata
    private int subscriberCount; // Number of subscribers for this resort
    
    // Summary of snow days
    private String snowSummary;
}
