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

```bash
docker logs lab3-producer
```

## Lab 4 - Kafka Streams aggregation

Overview
This project is a Java-based Kafka Streams application that processes raw travel data and aggregates it by the date of the trip. The architecture runs entirely in Docker and includes a Zookeeper instance, a 2-broker Kafka cluster, a data producer, the Kafka Streams processing application, and a Kafka UI for monitoring.

### Features

The Kafka Streams application consumes raw trip records from an input topic and computes the following metrics per day:

* Average Trip Duration: (topic-avg-duration)
* Total Trips: (topic-total-trips)
* Top Starting Station: The most popular initial station (topic-top-start-station)
* Top 3 Stations: The three most frequently used stations overall, including both start and end points (topic-top3-stations)

### Infrastructure

The environment is containerized using Docker Compose and consists of:

* Zookeeper: lab4-zookeeper (Port 2181)
* Kafka Brokers: lab4-broker1, lab4-broker2 (Ports 9092, 9093)
* Kafka UI: lab4-kafka-ui (Port 8080)
* Producer: lab4-producer (Python script to generate raw data)
* Streams App: lab4-streams-app (Java application running the topology)

### How to Run

#### Start the Cluster

Use Docker Compose to build the images and start the containers in the background.

```bash
docker-compose up -d --build
```

#### Monitor the Data

Open your browser and navigate to the Kafka UI: http://localhost:8080
From here, you can inspect the lab-cluster, view active brokers, and monitor the messages flowing into the four output aggregation topics.

#### Stop the Cluster

To safely tear down the environment and remove the containers:

```bash
docker-compose down
```
