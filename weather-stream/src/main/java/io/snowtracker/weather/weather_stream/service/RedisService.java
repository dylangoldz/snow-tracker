package io.snowtracker.weather.weather_stream.service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public List<Resort> getAllResorts() {
        return (List<Resort>) resortRepository.findAll();
    }
    
    /**
     * Get all phone numbers of users subscribed to a specific resort
     * @param resortId The ID of the resort
     * @return List of phone numbers subscribed to the resort
     */
    public List<String> getSubscribersForResort(String resortId) {
        // Get all users
        Iterable<User> allUsers = userRepository.findAll();
        
        // Filter users who are subscribed to this resort and collect their phone numbers
        return StreamSupport.stream(allUsers.spliterator(), false)
            .filter(user -> user.getResorts() != null && user.getResorts().contains(resortId))
            .map(User::getPhoneNumber)
            .collect(Collectors.toList());
    }
}
