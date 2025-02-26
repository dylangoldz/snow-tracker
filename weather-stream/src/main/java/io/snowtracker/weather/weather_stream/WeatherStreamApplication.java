package io.snowtracker.weather.weather_stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.snowtracker.weather.weather_stream.weather_proxy.NOAAWeatherProxy;

@SpringBootApplication
public class WeatherStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherStreamApplication.class, args);
		
	}

	// @Bean
    // public CommandLineRunner run(NOAAWeatherProxy noaaWeatherProxy) {
    //     return args -> {
	// 		//TODO remove proxy test
    //         noaaWeatherProxy.getForecast("OKX", "40.6413", "40.641340.6413")
	// 			.doOnNext(forecastResponse -> System.out.println(forecastResponse))
	// 			.subscribe();
    //     };
    // }

}
