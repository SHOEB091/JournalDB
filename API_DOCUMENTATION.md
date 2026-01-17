# Journal App - Complete API Documentation

## 🏗️ Architecture Overview

This is a Spring Boot application with a layered architecture:

### 📁 Project Structure
```
src/main/java/shoebdev/JournalAPP/
├── controller/          # REST API endpoints
│   ├── PublicController.java    # Public endpoints (signup, login, health)
│   ├── UserController.java      # User management (CRUD operations)
│   ├── JournalEntryControllerV2.java  # Journal entries management
│   └── HealthCheck.java         # Health check endpoint
├── service/            # Business logic layer
│   ├── UserService.java         # User-related business logic
│   ├── UserDetailsServiceImpl.java  # Spring Security user details
│   └── JournalEntryService.java # Journal entries business logic
├── repository/         # Data access layer
│   ├── UserRepository.java      # User data access
│   └── JournalEntryRepository.java  # Journal entries data access
├── entity/             # Data models (JPA entities)
│   ├── User.java               # User entity
│   └── JournalEntry.java       # Journal entry entity
└── config/             # Configuration classes
    └── SpringSecurity.java     # Security configuration
```

### 🔄 Data Flow
1. **Controller Layer**: Receives HTTP requests, validates input, returns responses
2. **Service Layer**: Contains business logic, orchestrates operations
3. **Repository Layer**: Handles database operations
4. **Entity Layer**: Defines data models and relationships

## 🔐 Security Configuration

### Public Endpoints (No Authentication Required)
- `GET /journal/**` - Journal entries (read-only)
- `POST /public/signup` - User registration
- `POST /public/login` - User login
- `POST /public/logout` - User logout
- `GET /public/health` - Health check

### Protected Endpoints (Authentication Required)
- `GET /user/**` - User management operations
- `POST /user/**` - User management operations
- `PUT /user/**` - User management operations
- `DELETE /user/**` - User management operations

### Authentication Methods
- **HTTP Basic Authentication** (for API clients)
- **Form-based Login** (for web browsers)

## 📋 API Endpoints

### 🔓 Public Endpoints

#### User Registration
```http
POST /public/signup
Content-Type: application/json

{
  "userName": "johndoe",
  "password": "securepassword"
}
```

#### User Login
```http
POST /public/login
Content-Type: application/json

{
  "userName": "johndoe",
  "password": "securepassword"
}
```

#### Health Check
```http
GET /public/health
```

### 🔒 Protected Endpoints (Require Authentication)

#### User Management

**Get Current User Profile**
```http
GET /user/profile
Authorization: Basic <base64-encoded-credentials>
```

**Update Current User Profile**
```http
PUT /user/profile
Content-Type: application/json
Authorization: Basic <base64-encoded-credentials>

{
  "userName": "newusername"
}
```

**Delete Current User Account**
```http
DELETE /user/profile
Authorization: Basic <base64-encoded-credentials>
```

**Get All Users (Admin)**
```http
GET /user
Authorization: Basic <base64-encoded-credentials>
```

**Get User by ID (Admin)**
```http
GET /user/id/{objectId}
Authorization: Basic <base64-encoded-credentials>
```

**Update User by ID (Admin)**
```http
PUT /user/id/{objectId}
Content-Type: application/json
Authorization: Basic <base64-encoded-credentials>

{
  "userName": "updatedname",
  "password": "newpassword",
  "roles": ["USER", "ADMIN"]
}
```

**Delete User by ID (Admin)**
```http
DELETE /user/id/{objectId}
Authorization: Basic <base64-encoded-credentials>
```

#### Journal Entries

**Get Journal Entries by Username**
```http
GET /journal/{userName}
```

**Create Journal Entry**
```http
POST /journal/{userName}
Content-Type: application/json
Authorization: Basic <base64-encoded-credentials>

{
  "title": "My Journal Entry",
  "content": "This is the content of my journal entry."
}
```

**Update Journal Entry**
```http
PUT /journal/{userName}/{id}
Content-Type: application/json
Authorization: Basic <base64-encoded-credentials>

{
  "title": "Updated Title",
  "content": "Updated content."
}
```

**Delete Journal Entry**
```http
DELETE /journal/{userName}/{id}
Authorization: Basic <base64-encoded-credentials>
```

## 🗄️ Database Schema

### User Collection
```javascript
{
  "_id": ObjectId("..."),
  "userName": "johndoe",
  "password": "$2a$10$...", // BCrypt hashed
  "roles": ["USER"],
  "journalEntries": [
    DBRef("journal_entries", ObjectId("..."))
  ]
}
```

### Journal Entry Collection
```javascript
{
  "_id": ObjectId("..."),
  "title": "My Journal Entry",
  "content": "Entry content",
  "date": "2026-01-17T12:00:00.000Z"
}
```

## 🔧 Key Components Explained

### Controllers
- **PublicController**: Handles public operations like signup, login, logout
- **UserController**: Manages user CRUD operations with proper authorization
- **JournalEntryControllerV2**: Manages journal entries with user association

### Services
- **UserService**: Core user business logic, password encoding, user operations
- **UserDetailsServiceImpl**: Implements Spring Security's UserDetailsService for authentication
- **JournalEntryService**: Journal entry business logic

### Repositories
- **UserRepository**: Extends MongoRepository for User entity operations
- **JournalEntryRepository**: Extends MongoRepository for JournalEntry operations

### Configuration
- **SpringSecurity**: Configures authentication, authorization, and public endpoints

## 🚀 Getting Started

1. **Start MongoDB**: Ensure MongoDB is running on localhost:27017
2. **Run Application**: `mvn spring-boot:run`
3. **Register User**: POST to `/public/signup`
4. **Login**: POST to `/public/login` or use HTTP Basic Auth
5. **Use Protected APIs**: Include Authorization header

## 🔒 Security Features

- **Password Hashing**: BCrypt encryption for all passwords
- **Role-based Access**: USER and ADMIN roles
- **Stateless Authentication**: No server-side sessions
- **CSRF Protection**: Disabled for API usage
- **Input Validation**: Basic validation on user input

## 🧪 Testing with Postman

1. **Register**: POST `/public/signup` with user credentials
2. **Login**: POST `/public/login` or use Basic Auth in subsequent requests
3. **Test Protected Endpoints**: Include Authorization header
4. **Journal Operations**: Use authenticated requests for CRUD operations

This architecture provides a secure, scalable REST API with proper separation of concerns and comprehensive user management capabilities.</content>
<parameter name="filePath">/Users/shoebiqbal/Downloads/JournalAPP/README.md