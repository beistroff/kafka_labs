# Kafka labs

## Lab 3 - Basics

Provisioning of an Apache Kafka cluster (Zookeeper + 2 Brokers), Kafka UI, and a Python producer using Docker Compose.

### Project Structure

The project consists of a containerized Kafka infrastructure and a custom Python microservice that reads server metrics from a CSV file (pandas) and publishes them as JSON events to two separate topics.

### How to run

```shell
docker compose up -d --build
```

Verification (Confirms messages are being read and sent):
```
docker logs lab3-producer
```
