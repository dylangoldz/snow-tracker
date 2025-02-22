package io.snowtracker.weather.weather_stream.redis_repository;

import org.springframework.data.repository.CrudRepository;

import io.snowtracker.weather.weather_stream.models.User;

public interface UserRepository extends CrudRepository<User,String>{
    
}
