# Product Service

A Spring Boot application for managing products, supporting CRUD operations, search, and caching with Redis.

## Features

- RESTful API for product management
- Integration with Fake Store API
- Caching with Redis for improved performance
- Product search with pagination and sorting
- Exception handling

## Technologies

- Java
- Spring Boot
- Spring Data JPA
- Redis
- Maven

## Getting Started

### Prerequisites

- Java 17+
- Maven
- Redis server

### Setup

1. Clone the repository:

`git clone https://github.com/NithinBommerla/ProductService.git cd productservice`

2.Configure application properties in `src/main/resources/application.properties`.

3. Start Redis server locally.

4. Build and run the application:
```mvn spring-boot:run```

5. ## API Endpoints

- `GET /products/{id}`: Get product by ID
- `GET /products`: Get all products
- `POST /products`: Create a new product
- `PUT /products/{id}`: Replace product by ID
- `PATCH /products/{id}`: Patch product by ID
- `GET /search`: Search products by name
