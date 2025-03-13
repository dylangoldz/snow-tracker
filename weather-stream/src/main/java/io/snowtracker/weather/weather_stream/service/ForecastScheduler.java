package io.snowtracker.weather.weather_stream.service;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.snowtracker.weather.weather_stream.models.Forecast;
import io.snowtracker.weather.weather_stream.models.Resort;
import io.snowtracker.weather.weather_stream.util.ForecastParser;
import io.snowtracker.weather.weather_stream.weather_proxy.NOAAWeatherProxy;
import io.snowtracker.weather.weather_stream.weather_proxy.dtos.ForecastResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ForecastScheduler {

    private final NOAAWeatherProxy proxy; 
    private final RedisService redisService;
    private final KafkaTemplate<String, Forecast> kafkaTemplate;
    private static final String TOPIC = "forecast_updates.all";
    private static final int BATCH_SIZE = 10;
    // Minimum snowfall in inches to trigger logging
    private static final double SNOW_THRESHOLD = 1.0;

    public ForecastScheduler(NOAAWeatherProxy proxy, RedisService redisService, KafkaTemplate<String, Forecast> kafkaTemplate) {
        this.proxy = proxy;
        this.redisService = redisService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void fetchAndPublishWeatherUpdates() {
        List<Resort> resorts = redisService.getAllResorts();
        
        // Process all resorts with controlled concurrency
        Flux.fromIterable(resorts)
            .parallel(BATCH_SIZE)  // Process up to BATCH_SIZE resorts concurrently
            .runOn(Schedulers.boundedElastic())  // Use bounded elastic scheduler for backpressure
            .flatMap(resort -> 
                fetchWeatherDataForResort(resort)
                    .map(forecastResponse -> {
                        Forecast forecast = ForecastParser.parseForecast(forecastResponse);
                        if (forecast != null) {
                            // Add resort information to the forecast
                            forecast.setResortId(resort.getId());
                            forecast.setResortName(resort.getName());
                            forecast.setRegionId(resort.getRegionId());
                            
                            // Count subscribers for logging purposes
                            int subscriberCount = redisService.getSubscribersForResort(resort.getId()).size();
                            forecast.setSubscriberCount(subscriberCount);
                            
                            return forecast;
                        }
                        return null;
                    })
                    .onErrorResume(e -> {
                        System.err.println("Error processing resort " + resort.getId() + ": " + e.getMessage());
                        return Mono.empty();
                    })
            )
            .filter(forecast -> forecast != null) // Only publish non-null forecasts
            .doOnNext(forecast -> {
                // Publish forecast to Kafka
                kafkaTemplate.send(TOPIC, forecast.getResortId(), forecast);
                
                // Log with additional details if snow is expected
                if (forecast.getTotalSnowfall() >= SNOW_THRESHOLD) {
                    System.out.println("Published forecast for " + forecast.getResortName() + 
                                      " with " + forecast.getSubscriberCount() + " subscribers. " +
                                      "Snow expected: " + forecast.getTotalSnowfall() + " inches total. " +
                                      forecast.getSnowSummary());
                } else {
                    System.out.println("Published forecast for " + forecast.getResortName() + 
                                      " with " + forecast.getSubscriberCount() + " subscribers. No significant snow expected.");
                }
            })
            .then()
            .block();  // Block until all processing is complete
    }
    
    private Mono<ForecastResponse> fetchWeatherDataForResort(Resort resort) {
        return proxy.getForecast(resort.getOffice(), resort.getGridX(), resort.getGridY())
            .onErrorResume(error -> {
                System.err.println("Error fetching weather for resort " + resort.getId() + ": " + error.getMessage());
                return Mono.empty(); // Handle errors gracefully by returning empty Mono
            });
    }
}
