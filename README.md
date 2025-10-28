## Project Name
Pay Nexus

## Technologies
**Backend:** Java (Spring Boot)
**Database**: H2 (in memory database)
**Containerization**: Docker

## Description
PayNexus is a backend application, microservices-based, which faciliates the exchange of traditional (fiat) and crypto currencies. The application itself implements a full-service arhitecture with separate microservices for user management, currency and crypto exchange, account management as well as trading operations. 

## Key functionalities
- User management system via API Gateway and Spring Security
- Crypto and fiat currencies exchange.
- Fault-tolerant (Chaos monkey, retry and circuit breaker) microservices architecture, powered by Spring Boot and H2 in-memory databases.
- Containerized deployment using Docker and orchestration via Docker Compose.

## Running the app

## Prerequisites
Docker desktop is installed

## 1 Start all services
Navigate to the folder containing `docker-compose.yaml` and run:

bash docker-compose up -d

## 2 Test the application via postman or other softvare
All endpoints are listed in Update API Documentation.md file


## 3. Stop all services
docker-compose down
