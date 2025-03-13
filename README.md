# Snow Tracker

A real-time snow notification system that monitors weather forecasts and sends SMS alerts to subscribers when significant snowfall is expected at their favorite ski resorts.

## System Architecture

The Snow Tracker system consists of the following components:

1. **Weather Data Collection**:
   - Hourly polling of the NOAA Weather API
   - Parsing and analysis of forecast data
   - Detection of significant snowfall events

2. **Notification Pipeline**:
   - Kafka-based messaging system for reliable delivery
   - Single topic with resort-specific notifications
   - Each notification includes subscriber phone numbers

## Data Model

- **Resorts**: Ski resorts with location data for weather forecasts
- **Users**: Subscribers with phone numbers and resort preferences
- **Forecasts**: Weather predictions including snowfall amounts
- **Notifications**: Messages with resort data and subscriber information

## Getting Started

### Prerequisites

- Java 17+
- Docker and Docker Compose
- Maven

### Running the System

1. Start the infrastructure:
   ```
   cd weather-stream/docker
   docker-compose up -d
   ```

2. Build and run the application:
   ```
   cd weather-stream
   ./mvnw spring-boot:run
   ```

3. Access the Kafka UI:
   - Open http://localhost:8080 in your browser
   - Monitor topics and messages

## Configuration

Key configuration options in `application.properties`:

- `spring.kafka.bootstrap-servers`: Kafka connection
- `spring.kafka.topic.name`: Topic for weather notifications
- `scheduler.forecast.rate`: Frequency of weather checks (ms)
- `sms.enabled`: Toggle SMS sending (default: false)

## Adding Subscribers

Users can subscribe to resorts through the API:

```
POST /api/users
{
  "fullName": "John Doe",
  "phoneNumber": "+15551234567",
  "resorts": ["vail:colorado", "breckenridge:colorado"]
}
```

## Extending the System

- Implement a real SMS provider (Twilio, AWS SNS, etc.)
- Add a web UI for user subscription management
- Implement historical data analysis for trend detection