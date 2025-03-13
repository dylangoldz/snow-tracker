package io.snowtracker.weather.weather_stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class WeatherStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherStreamApplication.class, args);
	}
}
