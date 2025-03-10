package io.snowtracker.weather.weather_stream.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherNotification {
        private String resortId;
        private String regionId;
        private String forecast;
        private List<String> phoneNumbers;
}
