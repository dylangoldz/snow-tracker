package io.snowtracker.weather.weather_stream.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.snowtracker.weather.weather_stream.models.DailyForecast;
import io.snowtracker.weather.weather_stream.models.Forecast;
import io.snowtracker.weather.weather_stream.weather_proxy.dtos.ForecastResponse;

/**
 * Utility class to parse NOAA forecast data and extract relevant information
 */
public class ForecastParser {

    // Pattern to match snow accumulation in forecast text
    private static final Pattern SNOW_PATTERN = Pattern.compile("(?i)new snow accumulation of (\\d+\\.?\\d*) to (\\d+\\.?\\d*) inch(?:es)?|(?i)new snow accumulation of (\\d+\\.?\\d*) inch(?:es)?|(?i)snow accumulation of (\\d+\\.?\\d*) to (\\d+\\.?\\d*) inch(?:es)?|(?i)snow accumulation of (\\d+\\.?\\d*) inch(?:es)?");

    /**
     * Parse a NOAA forecast response into our Forecast model
     * @param response The NOAA API response
     * @return A Forecast object with extracted data for all periods
     */
    public static Forecast parseForecast(ForecastResponse response) {
        if (response == null || response.getProperties() == null || 
            response.getProperties().getPeriods() == null || 
            response.getProperties().getPeriods().isEmpty()) {
            return null;
        }

        List<ForecastResponse.Properties.Period> periods = response.getProperties().getPeriods();
        List<DailyForecast> dailyForecasts = new ArrayList<>();
        
        // Process each period in the forecast
        for (ForecastResponse.Properties.Period period : periods) {
            DailyForecast daily = new DailyForecast();
            daily.setPeriodName(period.getName());
            
            // Parse dates
            if (period.getStartTime() != null) {
                daily.setStartTime(ZonedDateTime.parse(period.getStartTime()));
            }
            
            if (period.getEndTime() != null) {
                daily.setEndTime(ZonedDateTime.parse(period.getEndTime()));
            }
            
            // Set basic forecast info
            daily.setTemperature(period.getTemperature());
            daily.setTemperatureUnit(period.getTemperatureUnit());
            daily.setShortForecast(period.getShortForecast());
            daily.setDetailedForecast(period.getDetailedForecast());
            
            // Extract snowfall information from detailed forecast
            daily.setSnowfall(extractSnowfall(period.getDetailedForecast()));
            
            dailyForecasts.add(daily);
        }
        
        // Create the overall forecast
        Forecast forecast = new Forecast();
        
        // Set the overall time period
        if (!dailyForecasts.isEmpty()) {
            forecast.setStartTime(dailyForecasts.get(0).getStartTime());
            forecast.setEndTime(dailyForecasts.get(dailyForecasts.size() - 1).getEndTime());
        }
        
        // Calculate total snowfall
        double totalSnowfall = dailyForecasts.stream()
                .mapToDouble(DailyForecast::getSnowfall)
                .sum();
        forecast.setTotalSnowfall(totalSnowfall);
        
        // Set daily forecasts
        forecast.setDailyForecasts(dailyForecasts);
        
        // Create snow summary
        createSnowSummary(forecast);
        
        return forecast;
    }
    
    /**
     * Create a summary of snow days for the forecast
     */
    private static void createSnowSummary(Forecast forecast) {
        List<DailyForecast> snowDays = forecast.getDailyForecasts().stream()
                .filter(day -> day.getSnowfall() > 0)
                .collect(Collectors.toList());
        
        if (snowDays.isEmpty()) {
            forecast.setSnowSummary("No snow expected in the forecast period.");
            return;
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Snow expected on: ");
        
        for (int i = 0; i < snowDays.size(); i++) {
            DailyForecast day = snowDays.get(i);
            summary.append(day.getPeriodName())
                   .append(" (")
                   .append(String.format("%.1f", day.getSnowfall()))
                   .append(" inches)");
            
            if (i < snowDays.size() - 1) {
                summary.append(", ");
            }
        }
        
        forecast.setSnowSummary(summary.toString());
    }
    
    /**
     * Extract snowfall amount from forecast text
     * @param forecastText The detailed forecast text
     * @return Estimated snowfall in inches (0 if no snow mentioned)
     */
    private static double extractSnowfall(String forecastText) {
        if (forecastText == null || forecastText.isEmpty()) {
            return 0.0;
        }
        
        Matcher matcher = SNOW_PATTERN.matcher(forecastText);
        if (matcher.find()) {
            // Check which pattern matched
            if (matcher.group(1) != null && matcher.group(2) != null) {
                // Range: "new snow accumulation of X to Y inches"
                double min = Double.parseDouble(matcher.group(1));
                double max = Double.parseDouble(matcher.group(2));
                return (min + max) / 2; // Return average
            } else if (matcher.group(3) != null) {
                // Single value: "new snow accumulation of X inches"
                return Double.parseDouble(matcher.group(3));
            } else if (matcher.group(4) != null && matcher.group(5) != null) {
                // Range: "snow accumulation of X to Y inches"
                double min = Double.parseDouble(matcher.group(4));
                double max = Double.parseDouble(matcher.group(5));
                return (min + max) / 2; // Return average
            } else if (matcher.group(6) != null) {
                // Single value: "snow accumulation of X inches"
                return Double.parseDouble(matcher.group(6));
            }
        }
        
        // Check for keywords indicating snow but no specific amount
        if (forecastText.toLowerCase().contains("snow") || 
            forecastText.toLowerCase().contains("flurries")) {
            return 0.5; // Default to light snow (0.5 inches) if snow is mentioned but no amount
        }
        
        return 0.0; // No snow mentioned
    }
} 