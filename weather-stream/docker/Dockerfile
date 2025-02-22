# Use the official Redis image
FROM redis:latest

# Copy custom redis.conf into the container
COPY redis.conf /usr/local/etc/redis/redis.conf

# Set Redis to use the custom configuration file
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]
