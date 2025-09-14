# Multi-stage build for Sankshipt Auth Server
# NOTE: This Dockerfile should be run from the project root directory
FROM maven:3.9.11-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy parent pom first for better caching
COPY pom.xml ./pom.xml

# Copy auth-server pom
COPY auth-server/pom.xml ./auth-server/pom.xml

# Download dependencies (this layer will be cached if pom.xml doesn't change)
WORKDIR /app/auth-server
RUN mvn dependency:go-offline -B

# Copy source code
COPY auth-server/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

# Create app directory
WORKDIR /app

# Copy the built jar file
COPY --from=build /app/auth-server/target/sankshipt-auth-*.jar sankshipt-auth.jar

# Expose port (Auth server port)
EXPOSE 9000

# Environment variables for database configuration
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/sankshipt_auth_db
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=password

# Run the application
ENTRYPOINT ["java", "-jar", "sankshipt-auth.jar"]
