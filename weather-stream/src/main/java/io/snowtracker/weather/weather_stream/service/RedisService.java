package io.snowtracker.weather.weather_stream.service;

import org.springframework.stereotype.Service;

import io.snowtracker.weather.weather_stream.models.Resort;
import io.snowtracker.weather.weather_stream.models.User;
import io.snowtracker.weather.weather_stream.redis_repository.ResortRepository;
import io.snowtracker.weather.weather_stream.redis_repository.UserRepository;

@Service
public class RedisService {

    private final UserRepository userRepository;
    private final ResortRepository resortRepository;

    public RedisService(UserRepository userRepository, ResortRepository resortRepository) {
        this.userRepository = userRepository;
        this.resortRepository = resortRepository;
    }

    // Store user
    public void saveUser(User user) {
        userRepository.save(user);
    }

    // Retrieve user
    public User getUser(String id) {
        return userRepository.findById(id).orElse(null);
    }

    // Store resort
    public void saveResort(Resort resort) {
        resortRepository.save(resort);
    }

    // Retrieve resort
    public Resort getResort(String id) {
        return resortRepository.findById(id).orElse(null);
    }
}
