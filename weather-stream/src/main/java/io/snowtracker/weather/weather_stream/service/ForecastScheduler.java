package io.snowtracker.weather.weather_stream.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.snowtracker.weather.weather_stream.models.Forecast;
import io.snowtracker.weather.weather_stream.models.Resort;
import io.snowtracker.weather.weather_stream.models.WeatherNotification;
import io.snowtracker.weather.weather_stream.weather_proxy.NOAAWeatherProxy;
import io.snowtracker.weather.weather_stream.weather_proxy.dtos.ForecastResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ForecastScheduler {

    private final NOAAWeatherProxy proxy; 
    private final RedisService redisService;
    private final KafkaTemplate<String,WeatherNotification> kafkaTemplate;
    private static final String TOPIC = "forecast_updates.all";
    private static final int BATCH_SIZE = 10;

    public ForecastScheduler(NOAAWeatherProxy proxy, RedisService redisService, KafkaTemplate<String,WeatherNotification> kafkaTemplate) {
        this.proxy = proxy;
        this.redisService = redisService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 3600000)
    public void fetchAndPublishWeatherUpdates() {
        
        List<Resort> resorts = redisService.getAllResorts();

        List<List<Resort>> batches = IntStream.range(0,(resorts.size() + BATCH_SIZE -1) / BATCH_SIZE)
                .mapToObj(i -> resorts.subList(i * BATCH_SIZE, Math.min(resorts.size(), (i + 1) * BATCH_SIZE)))
                .collect(Collectors.toList());
        
        // Process each batch in parallel
        List<CompletableFuture<Void>> futures = batches.stream()
        .map(this::processBatchAsync)
        .collect(Collectors.toList());

        // Wait for all batches to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
    }

    @Async
    private CompletableFuture<Void> processBatchAsync(List<Resort> batch) {
        return Flux.fromIterable(batch)
            .flatMap(resort -> fetchWeatherDataForResort(resort)) // Fetch weather in parallel
                .map(forecast -> new WeatherNotification(resort.getRegionId, TOPIC, TOPIC, null)) // Pair Resort with Forecast
            .collectList() // Gather all results into a List
            .doOnSuccess(resortWeatherList -> {
                for (WeatherNotification notification : resortWeatherList) {
                    if (rw.forecast != null) {
                        List<String> phoneNumbers = redisService.getSubscribersForResort(rw.resort.getId());
    
                        WeatherNotification notification = new WeatherNotification(
                            rw.resort.getId(),
                            rw.resort.getRegionId(),
                            String.format("Snow Alert! %d inches at %s.", rw.forecast.getSnowfall(), rw.resort.getName()),
                            phoneNumbers
                        );
    
                        kafkaTemplate.send(TOPIC, rw.resort.getId(), notification);
                    }
                }
            })
            .then() // Converts Mono<Void> to CompletableFuture<Void>
            .toFuture();
    }
    
    private Mono<ForecastResponse> fetchWeatherDataForResort(Resort resort) {
        return proxy.getForecast(resort.getOffice(), resort.getGridX(), resort.getGridY())
            .onErrorResume(error -> {
                System.err.println("Error fetching weather for resort " + resort.getId() + ": " + error.getMessage());
                return Mono.empty(); // Handle errors gracefully by returning empty Mono
            });
    }

}
