package io.snowtracker.weather.weather_stream.redis_repository;

import org.springframework.data.repository.CrudRepository;

import io.snowtracker.weather.weather_stream.models.Resort;

public interface ResortRepository extends CrudRepository<Resort,String> {
    
}
