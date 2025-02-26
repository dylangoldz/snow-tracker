package io.snowtracker.weather.weather_stream.config;

import java.io.InputStream;
import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.snowtracker.weather.weather_stream.models.Resort;
import io.snowtracker.weather.weather_stream.redis_repository.ResortRepository;

@Configuration
public class RedisDataLoader {
    
    private final ResortRepository resortRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisDataLoader(ResortRepository resortRepository) {
        this.resortRepository = resortRepository;
        System.out.println("RedisDataLoader initialized.");
    }

    @Bean
    ApplicationRunner loadResortData() {
        return args -> {
            System.out.println("Loading resort data...");
            try (InputStream inputStream = getClass().getResourceAsStream("/resort_data.json")) {
                List<Resort> resorts = objectMapper.readValue(inputStream, new TypeReference<List<Resort>>() {});
                resorts.forEach(resort -> resort.setId(generateId(resort))); // Generate IDs
                resortRepository.saveAll(resorts);
                System.out.println("✅ Resorts preloaded into Redis.");
            } catch (Exception e) {
                System.err.println("❌ Failed to preload resorts: " + e.getMessage());
            }
        }; 
    }

    private String generateId(Resort resort) {
        return String.join(":", resort.getName().toLowerCase().replaceAll("\\s+", "-"),
                resort.getRegionId().toLowerCase());
    }
}
