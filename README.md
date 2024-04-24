# vinicius-pessan-backend
Projeto backend - Vinicius Pessan 

# Requirements
Java 17+
Docker

To utilize this API, simply adhere to the following instructions:

# 1 - Preliminary Setup - Establishing our project

Execute the command mvn install to install the required dependencies.

Run mvn package to generate a JAR file for creating Docker containers.

# 2 - Docker Containerization

Build Docker containers by executing docker-compose -f docker-compose.yml build.

Start Docker containers in detached mode using docker-compose -f docker-compose.yml up -d.

You can now access comprehensive documentation via the Swagger UI:

User API: http://localhost:8081/api/swagger-ui/index.html#/
Integration API: http://localhost:8082/api/swagger-ui/index.html#/


