# 🔗 Sankshipt - URL Shortener Service

**Sankshipt** is a sophisticated, enterprise-grade URL shortener built with Java Spring Boot that provides secure link generation, comprehensive analytics, and robust authentication. The service generates short links, tracks clicks with detailed metadata, and provides comprehensive analytics for your URLs.

## 🏗️ Tech Stack

### Backend Technologies
- **Java 17** - Modern Java features and performance
- **Spring Boot 3.5.5** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction layer
- **Hibernate** - ORM for database operations
- **OAuth2 Authorization Server** - Token-based authentication
- **OAuth2 Resource Server** - JWT token validation

### Database
- **MySQL 8.0** - Primary database for data persistence
- **Separate databases** for auth and API services for security isolation

### Documentation & API
- **SpringDoc OpenAPI 3** - API documentation
- **Swagger UI** - Interactive API testing interface

### Containerization & Orchestration
- **Docker** - Containerization
- **Docker Compose** - Multi-service orchestration

### Security Features
- **JWT tokens** - Stateless authentication
- **OAuth2 flows** - Standard authorization protocols
- **Role-based access control** - ADMIN/USER roles
- **Scope-based permissions** - Fine-grained API access control
- **MD5 checksum validation** - URL integrity verification

### Development Tools
- **Lombok** - Reduces boilerplate code
- **Maven** - Dependency management and build tool
- **JUnit 5 & Mockito** - Testing framework

## 🚀 How to Run

### Prerequisites
- Docker and Docker Compose installed
- Port 8080 (API), 9000 (Auth), and 3306 (MySQL) available

### Quick Start with Docker Compose

1. **Clone the repository**
   ```bash
   git clone https://github.com/hitanshu-dhawan/Sankshipt.git
   cd Sankshipt
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Verify services are running**
   ```bash
   docker-compose ps
   ```

### Service Endpoints
- **API Server**: http://localhost:8080
- **Auth Server**: http://localhost:9000
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Documentation**: http://localhost:8080/api-docs

### Database Setup
The services automatically create their databases:
- `sankshipt_db` - Main application data
- `sankshipt_auth_db` - Authentication data

## 📡 API Endpoints

### 🔐 Authentication Endpoints (Auth Server - Port 9000)
- `POST /api/users/signup` - Register new user
- `POST /api/users/signin` - User login
- `POST /api/users/signout` - User logout
- `GET /api/users/validate/{token}` - Validate user token

### 🔗 URL Management Endpoints (API Server - Port 8080)
- `POST /api/urls` - Create short URL *(requires `api.write` scope)*
- `DELETE /api/urls` - Delete short URL *(requires `api.delete` scope)*
- `GET /{shortCode}` - Redirect to original URL *(public)*

### 📊 Analytics Endpoints (API Server - Port 8080)
- `GET /api/analytics/{shortCode}/count` - Get click count *(requires `api.read` scope)*
- `GET /api/analytics/{shortCode}/clicks` - Get paginated click details *(requires `api.read` scope)*

### 📚 Documentation Endpoints
- `GET /swagger-ui.html` - Interactive API documentation
- `GET /api-docs` - OpenAPI specification

## 🔐 OAuth2 Authentication & Authorization

### OAuth2 Flow Implementation
Sankshipt implements the **Authorization Code Grant** flow with the following configuration:

- **Client ID**: `sankshipt-client`
- **Client Secret**: `sankshipt-client-secret`
- **Authorization URL**: `http://localhost:9000/oauth2/authorize`
- **Token URL**: `http://localhost:9000/oauth2/token`
- **Issuer**: `http://localhost:9000`

### Scopes & Permissions 🎯
The system implements fine-grained access control with three distinct scopes:

| Scope | Description | Endpoints |
|-------|-------------|-----------|
| `api.read` | Read access to analytics and URL data | GET analytics endpoints |
| `api.write` | Create new short URLs | POST /api/urls |
| `api.delete` | Delete existing short URLs | DELETE /api/urls |

### User Roles 👥
Two user roles are supported with different privilege levels:

| Role | Description | Default Scopes |
|------|-------------|----------------|
| `USER` | Regular user with limited permissions | Based on requested scopes |
| `ADMIN` | Administrative user with full access | **All scopes automatically granted** |

### JWT Token Structure
JWT tokens include:
- **Subject**: User email
- **Roles**: User's assigned roles
- **Scopes**: Granted permissions
- **Issuer**: Authorization server URL
- **Expiration**: Token validity period

## 📖 Swagger Integration

### Interactive API Documentation
Sankshipt provides comprehensive API documentation through Swagger UI with:

- **OAuth2 Integration**: Built-in authentication flow
- **Try-it-out**: Live API testing capabilities
- **Organized Endpoints**: Grouped by functionality
  - 1. URL Management
  - 2. URL Redirection  
  - 3. Analytics

### Accessing Swagger UI
1. Navigate to http://localhost:8080/swagger-ui.html
2. Click "Authorize" to authenticate via OAuth2
3. Grant required scopes based on the endpoints you want to test
4. Test endpoints directly from the documentation

### OAuth2 Configuration in Swagger
```yaml
springdoc:
  swagger-ui:
    oauth:
      client-id: sankshipt-client
      client-secret: sankshipt-client-secret
      use-basic-authentication-with-access-code-grant: true
```

## 🔒 Security Implementation

### Authentication Flow
1. **User Registration/Login** → Auth Server issues JWT token
2. **API Request** → Include Bearer token in Authorization header
3. **Token Validation** → API server validates JWT with Auth server
4. **Scope Verification** → Check if user has required permissions
5. **Resource Access** → Grant or deny based on ownership and scopes

### URL Security Features
- **Checksum Validation**: Each short code includes MD5 checksum of original URL
- **User Ownership**: Users can only manage their own URLs
- **Tamper Detection**: Invalid checksums trigger security errors

### Security Configuration
```java
// JWT-based stateless authentication
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
// OAuth2 resource server configuration
.oauth2ResourceServer(oauth2 -> oauth2.jwt(...))
// Method-level security
@PreAuthorize("hasAuthority('SCOPE_api.write')")
```

## 📊 Analytics & Click Tracking

### Comprehensive Click Analytics 📈
Sankshipt tracks detailed click analytics for every shortened URL:

#### Tracked Metadata
- **Timestamp**: Exact click time with precision
- **User Agent**: Browser, device, and OS information
- **Future Extensibility**: Ready for IP tracking, geolocation, referrer data

#### Analytics Features
- **Click Count**: Total clicks per short URL
- **Detailed Click History**: Paginated list of all clicks
- **Sorting Options**: Ascending/Descending by timestamp
- **User Isolation**: Users can only view analytics for their own URLs

#### Sample Analytics Data
```json
{
  "content": [
    {
      "id": 1,
      "shortCode": "8g5ababd",
      "originalUrl": "https://example.com",
      "clickedAt": "2025-09-15T10:30:45.123Z",
      "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
    }
  ],
  "pageable": {},
  "totalElements": 42
}
```

### Analytics API Usage
```bash
# Get total click count
GET /api/analytics/{shortCode}/count

# Get paginated click details with sorting
GET /api/analytics/{shortCode}/clicks?pageNumber=0&pageSize=10&sortOrder=DESC
```

## 🔧 Short Code Generation Algorithm

### Hybrid Algorithm: Base62 + MD5 Checksum
Sankshipt uses a sophisticated dual-approach for generating short codes:

#### Algorithm Components
1. **Base62 Encoding**: Database ID → Compact representation
2. **MD5 Checksum**: Original URL → 6-character security hash

#### Example Generation Process
```
URL ID: 500 → Base62: "8g"
Original URL: "https://example.com" → MD5: "5d41402a..." → Checksum: "5d4140"
Final Short Code: "8g5d4140"
```

#### Security Benefits
- **Tamper Detection**: Modified short codes fail checksum validation
- **Data Integrity**: Ensures short code matches stored URL
- **Collision Resistance**: MD5 component prevents ID reuse attacks

#### Algorithm Features
- **Deterministic**: Same inputs always produce same output
- **Reversible**: Can extract original ID from short code
- **Secure**: Checksum validation prevents manipulation
- **Compact**: Efficient Base62 encoding for short URLs

## 🏗️ Microservices Architecture

### Service Separation
Sankshipt follows a microservices architecture with clear separation of concerns:

#### Auth Server (Port 9000)
- **Purpose**: User authentication and JWT token management
- **Database**: `sankshipt_auth_db`
- **Responsibilities**:
  - User registration and login
  - OAuth2 authorization server
  - JWT token issuance and validation
  - Role and scope management

#### API Server (Port 8080)
- **Purpose**: Core business logic and URL management
- **Database**: `sankshipt_db`
- **Responsibilities**:
  - URL shortening and expansion
  - Click analytics and tracking
  - User authorization and ownership validation
  - API documentation via Swagger

### Inter-Service Communication
- **JWT Validation**: API server validates tokens with Auth server
- **Network Isolation**: Services communicate via Docker network
- **Health Checks**: Ensures service dependencies are ready

## 🐳 Docker Configuration

### Multi-Service Setup
```yaml
services:
  mysql:          # Shared database for both services
  auth-server:    # Authentication & authorization
  api-server:     # Main application logic
```

### Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/sankshipt_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# OAuth Configuration  
OAUTH_ISSUER_URI=http://auth-server:9000
```

### Health Checks & Dependencies
- **MySQL Health Check**: Ensures database readiness
- **Service Dependencies**: API server waits for Auth server
- **Graceful Startup**: Proper initialization order

## 🧪 Testing

### Comprehensive Test Coverage
- **Unit Tests**: Service layer testing with Mockito
- **Integration Tests**: Controller testing with MockMvc
- **Security Tests**: Authentication and authorization scenarios
- **Algorithm Tests**: Short code generation and validation

### Test Examples
```java
@Test
@WithMockUser(authorities = "SCOPE_api.write")
void createShortUrl_ValidRequest_ShouldReturnCreatedUrl()

@Test
@WithMockUser(authorities = "SCOPE_api.delete")
void deleteShortUrl_NotOwner_ShouldReturnForbidden()
```

## 🚀 Getting Started Guide

### 1. Setup Development Environment
```bash
# Clone repository
git clone https://github.com/hitanshu-dhawan/Sankshipt.git
cd Sankshipt

# Start services
docker-compose up --build
```

### 2. Test Authentication Flow
1. Open Swagger UI: http://localhost:8080/swagger-ui.html
2. Click "Authorize" and complete OAuth2 flow
3. Grant required scopes (api.read, api.write, api.delete)

### 3. Create Your First Short URL
```bash
curl -X POST "http://localhost:8080/api/urls" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://example.com"}'
```

### 4. Access Analytics
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/analytics/YOUR_SHORT_CODE/count"
```

## 📝 Configuration

### Application Configuration
Key configuration files:
- `docker-compose.yml` - Service orchestration
- `api-server/src/main/resources/application.yml` - API server config
- `auth-server/src/main/resources/application.yml` - Auth server config

### Environment Customization
Override default settings using environment variables:
```bash
# Custom database settings
export SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/your_db
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password

# Custom OAuth settings
export OAUTH_ISSUER_URI=https://your-auth-server.com
```

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Commit** your changes: `git commit -m 'Add amazing feature'`
4. **Push** to the branch: `git push origin feature/amazing-feature`
5. **Open** a Pull Request

### Development Standards
- Write comprehensive tests for new features
- Follow Spring Boot best practices
- Maintain API documentation
- Include security considerations

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏷️ Version Information

- **Version**: 0.0.1
- **Java**: 17
- **Spring Boot**: 3.5.5
- **API Documentation**: OpenAPI 3.0

---

**Built with ❤️ using Spring Boot and modern Java technologies**

For questions, issues, or feature requests, please open an issue on GitHub.
