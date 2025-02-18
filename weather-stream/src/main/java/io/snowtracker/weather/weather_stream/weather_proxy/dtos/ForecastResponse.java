package io.snowtracker.weather.weather_stream.weather_proxy.dtos;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@SuppressWarnings("unused")
public class ForecastResponse {
    @NotNull
    private String id;

    @NotNull
    private String type;

    @NotNull
    private Properties properties;

    @Data   
    public static class Properties {
        @NotNull
        private String geometry;

        @NotNull
        private String units;

        @NotNull
        private String forecastGenerator;

        @NotNull
        private String generatedAt;

        @NotNull
        private String updateTime;

        private Elevation elevation;

        @NotNull
        private List<Period> periods;

        public static class Elevation {
            @NotNull
            private double value;

            private double maxValue;
            private double minValue;

            @NotNull
            private String unitCode;

            private String qualityControl;
        }

        public static class Period {
            @NotNull
            private int number;

            @NotNull
            @Size(max = 100)
            private String name;

            @NotNull
            private String startTime;

            @NotNull
            private String endTime;

            private boolean isDaytime;

            private String temperatureTrend;

            private ProbabilityOfPrecipitation probabilityOfPrecipitation;
            private Dewpoint dewpoint;
            private RelativeHumidity relativeHumidity;

            @NotNull
            private String windDirection;

            @NotNull
            private String shortForecast;

            @NotNull
            private String detailedForecast;

            public static class ProbabilityOfPrecipitation {
                @NotNull
                private double value;

                private double maxValue;
                private double minValue;

                @NotNull
                private String unitCode;

                private String qualityControl;
            }

            public static class Dewpoint {
                @NotNull
                private double value;

                private double maxValue;
                private double minValue;

                @NotNull
                private String unitCode;

                private String qualityControl;
            }

            public static class RelativeHumidity {
                @NotNull
                private double value;

                private double maxValue;
                private double minValue;

                @NotNull
                private String unitCode;

                private String qualityControl;
            }
        }
    }
}
