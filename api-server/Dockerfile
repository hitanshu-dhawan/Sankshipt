# Multi-stage build for Sankshipt API Server
# NOTE: This Dockerfile should be run from the project root directory
FROM maven:3.9.11-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy parent pom first for better caching
COPY pom.xml ./pom.xml

# Copy api-server pom
COPY api-server/pom.xml ./api-server/pom.xml

# Download dependencies (this layer will be cached if pom.xml doesn't change)
WORKDIR /app/api-server
RUN mvn dependency:go-offline -B

# Copy source code
COPY api-server/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

# Create app directory
WORKDIR /app

# Copy the built jar file
COPY --from=build /app/api-server/target/sankshipt-api-*.jar sankshipt-api.jar

# Expose port (API server port)
EXPOSE 8080

# Environment variables for database and auth configuration
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/sankshipt_db
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=password

# Run the application
ENTRYPOINT ["java", "-jar", "sankshipt-api.jar"]
