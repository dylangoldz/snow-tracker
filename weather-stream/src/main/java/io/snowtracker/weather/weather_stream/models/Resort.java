package io.snowtracker.weather.weather_stream.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@RedisHash("Resort")
@Data
public class Resort {
    @Id
    private String id;
    private String name;
    private String regionId;
    private String office;
    private String gridX;
    private String girdY;
}
