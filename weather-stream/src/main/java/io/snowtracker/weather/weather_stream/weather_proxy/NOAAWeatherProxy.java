package io.snowtracker.weather.weather_stream.weather_proxy;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.snowtracker.weather.weather_stream.weather_proxy.dtos.ForecastResponse;
import reactor.core.publisher.Mono;

@Service
public class NOAAWeatherProxy {
    private static final String NOAA_API_BASE_URL = "https://api.weather.gov";
    private final WebClient webClient;

    public NOAAWeatherProxy() {
        this.webClient = WebClient.builder()
            .baseUrl(NOAA_API_BASE_URL)
            .defaultHeader( "User-Agent", "PowderTracker/1.0")
            .build();
    }

    public Mono<ForecastResponse> getForecast(String office, String gridX, String gridY) {
        return webClient.get()
        .uri("/gridpoints/{office}/{gridX}/{gridY}/forecast", office, gridX, gridY)
        .retrieve()
        .bodyToMono(ForecastResponse.class)
        .doOnError(error -> {
            System.err.println("Error fetching forecast: " + error.getMessage());
        });
    }


}
