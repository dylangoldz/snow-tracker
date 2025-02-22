package io.snowtracker.weather.weather_stream.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@RedisHash("User")
@Data
public class User {
    @Id
    private String id;
    private String fullName;
    private String phoneNumber;
    private List<String> regions;
    private List<String> resorts;
}
