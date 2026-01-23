# Industrial IoT Device Management Platform

A high-performance backend system simulating an Industry 4.0 environment. This platform manages IoT device identities, ingests high-frequency telemetry via Kafka, and stores time-series data in TimescaleDB.

## 🏗 Architecture
- **Language:** Java 21 (Spring Boot 4.0.1)
- **Database:** PostgreSQL + TimescaleDB (Time-series optimization)
- **Messaging:** Apache Kafka + Zookeeper
- **Security:** Spring Security (JWT) - *In Progress*
- **Containerization:** Docker & Docker Compose

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 21 SDK

### Running the Infrastructure
The system uses a hybrid development environment (Local Java + Dockerized Infra).

1. Start the containers (Postgres, Kafka, Zookeeper):
   ```bash
   docker-compose up -d