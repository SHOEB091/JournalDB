# 📖 Journal App - Complete Testing Guide

A Spring Boot REST API for managing personal journal entries with user authentication and authorization.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MongoDB running on `localhost:27017`

### Setup
```bash
# Clone the repository
git clone <your-repo-url>
cd JournalAPP

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

---

## 🏗️ Architecture Overview

### What We've Built
This is a **Spring Boot 3.x application** using **Spring Security 6** for authentication and authorization, with **MongoDB** as the database. The app allows users to create, read, update, and delete personal journal entries, with role-based access control (USER and ADMIN roles).

### Key Technologies
- **Spring Boot 3.5.8** - Framework for building REST APIs
- **Spring Security 6** - Authentication and authorization
- **MongoDB** - NoSQL database for data persistence
- **Spring Data MongoDB** - Repository layer for database operations
- **BCrypt** - Password hashing
- **Lombok** - Reduces boilerplate code

### File Structure & Connections

#### Main Application
- **`JournalAppApplication.java`** - Entry point, bootstraps the Spring Boot app with `@SpringBootApplication` and `@EnableTransactionManagement`

#### Entities (Data Models)
- **`User.java`** - Represents users with username, password, roles, and embedded journal entries
- **`JournalEntry.java`** - Represents journal entries with title, content, and timestamp

#### Repositories (Data Access Layer)
- **`UserRepository.java`** - Extends `MongoRepository<User, ObjectId>`, provides CRUD operations for users
- **`JournalEntryRepository.java`** - Extends `MongoRepository<JournalEntry, ObjectId>`, provides CRUD operations for journal entries

#### Services (Business Logic Layer)
- **`UserService.java`** - Handles user operations: registration, password encoding, user lookup
- **`JournalEntryService.java`** - Handles journal entry operations: save, find, delete
- **`UserDetailsServiceImpl.java`** - Implements Spring Security's `UserDetailsService` for authentication

#### Controllers (API Layer)
- **`PublicController.java`** - Public endpoints: signup, login, logout, health check
- **`UserController.java`** - User management: profile operations, admin-only user management
- **`JournalEntryControllerV2.java`** - Journal CRUD operations with authorization checks
- **`AdminController.java`** - Admin-only operations: user management, role updates
- **`HealthCheck.java`** - Simple health check endpoint

#### Configuration
- **`SpringSecurity.java`** - Security configuration using Spring Security 6 style:
  - `SecurityFilterChain` bean for HTTP security rules
  - `AuthenticationManager` bean for authentication
  - `PasswordEncoder` bean for password hashing
  - Authorization rules: `/public/**` permit all, `/user/**` authenticated, `/admin/**` admin role, `/journal/**` authenticated

### Data Flow
1. **Request** → Controller
2. **Controller** → Service (business logic)
3. **Service** → Repository (database operations)
4. **Repository** → MongoDB
5. **Response** flows back through the layers

### Authentication & Authorization

#### Authentication (Who are you?)
- Uses **Basic Authentication** (username/password)
- `UserDetailsServiceImpl` loads user details from database
- Passwords are hashed with BCrypt
- `AuthenticationManager` validates credentials

#### Authorization (What can you do?)
- **Role-based**: USER and ADMIN roles
- **Method-level security**: `@PreAuthorize("hasRole('ADMIN')")` on admin controller
- **URL-level security** in `SecurityFilterChain`:
  - `/public/**` - No authentication required
  - `/journal/**` - Authentication required
  - `/user/**` - Authentication required
  - `/admin/**` - ADMIN role required
- **Resource ownership**: Users can only access their own journal entries (unless admin)

#### Security Features
- **CSRF disabled** for API simplicity
- **Stateless sessions** (no server-side session storage)
- **Password encoding** with BCrypt
- **Role-based access control** for admin operations
- **Input validation** and error handling

### Database Schema
- **users** collection: Stores user data with embedded journal entries
- **journalEntries** collection: Stores individual journal entries (referenced from users)

---

## 📋 Complete Testing Flow

Follow these steps in order to test the entire application from start to end.

---

## 🔐 Phase 1: Authentication & User Management

### 1. Health Check (No Auth Required)
```http
GET /public/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "message": "Journal App is running",
  "timestamp": 1673894400000
}
```

### 2. User Registration (No Auth Required)
```http
POST /public/signup
Content-Type: application/json

{
  "userName": "johndoe",
  "password": "securepassword123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully"
}
```

### 3. User Login (No Auth Required)
```http
POST /public/login
Content-Type: application/json

{
  "userName": "johndoe",
  "password": "securepassword123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "userName": "johndoe",
    "roles": ["USER"]
  }
}
```

**Save the user ID for later use!**

---

## 👤 Phase 2: User Profile Management (Requires Authentication)

### 4. Get Current User Profile
```http
GET /user/profile
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
```

**Expected Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "userName": "johndoe",
  "roles": ["USER"],
  "journalEntries": []
}
```

### 5. Update Current User Profile
```http
PUT /user/profile
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
Content-Type: application/json

{
  "userName": "john_doe_updated"
}
```

**Expected Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "userName": "john_doe_updated",
  "roles": ["USER"],
  "journalEntries": []
}
```

---

## 📝 Phase 3: Journal Entry Management (Requires Authentication)

### 6. Create a Journal Entry
```http
POST /journal/john_doe_updated
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
Content-Type: application/json

{
  "title": "My First Journal Entry",
  "content": "Today was an amazing day. I learned about Spring Boot and created my first REST API!"
}
```

**Expected Response:**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "title": "My First Journal Entry",
  "content": "Today was an amazing day. I learned about Spring Boot and created my first REST API!",
  "date": "2026-01-17T15:30:00.000+00:00"
}
```

### 7. Get All Journal Entries for User
```http
GET /journal/john_doe_updated
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
```

**Expected Response:**
```json
[
  {
    "id": "507f1f77bcf86cd799439012",
    "title": "My First Journal Entry",
    "content": "Today was an amazing day. I learned about Spring Boot and created my first REST API!",
    "date": "2026-01-17T15:30:00.000+00:00"
  }
]
```

### 8. Create Another Journal Entry
```http
POST /journal/john_doe_updated
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
Content-Type: application/json

{
  "title": "Learning Authentication",
  "content": "Today I implemented JWT authentication and learned about Spring Security. It's quite powerful!"
}
```

### 9. Get Specific Journal Entry
```http
GET /journal/john_doe_updated/id/507f1f77bcf86cd799439012
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
```

**Expected Response:**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "title": "My First Journal Entry",
  "content": "Today was an amazing day. I learned about Spring Boot and created my first REST API!",
  "date": "2026-01-17T15:30:00.000+00:00"
}
```

### 10. Update Journal Entry
```http
PUT /journal/john_doe_updated/id/507f1f77bcf86cd799439012
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
Content-Type: application/json

{
  "title": "My First Journal Entry - Updated",
  "content": "Today was an amazing day. I learned about Spring Boot, created my first REST API, and implemented authentication!"
}
```

**Expected Response:**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "title": "My First Journal Entry - Updated",
  "content": "Today was an amazing day. I learned about Spring Boot, created my first REST API, and implemented authentication!",
  "date": "2026-01-17T15:30:00.000+00:00"
}
```

### 11. Delete Journal Entry
```http
DELETE /journal/john_doe_updated/id/507f1f77bcf86cd799439012
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Journal entry deleted successfully",
  "id": "507f1f77bcf86cd799439012"
}
```

---

## 🔍 Phase 4: Testing Authorization (Access Control)

### 12. Try to Access Another User's Journal (Should Fail)
```http
GET /journal/someotheruser
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
```

**Expected Response:** `403 Forbidden` or empty array (depending on configuration)

### 13. Try to Create Entry for Another User (Should Fail)
```http
POST /journal/someotheruser
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
Content-Type: application/json

{
  "title": "This Should Fail",
  "content": "Trying to create entry for another user"
}
```

**Expected Response:** `403 Forbidden`

### 14. Try Accessing Protected Endpoints Without Auth (Should Fail)
```http
GET /user/profile
```

**Expected Response:** `401 Unauthorized`

---

## 👑 Phase 5: Admin Operations (If you have admin user)

### 15. Create Admin User (Manual DB insertion or modify existing user)
```javascript
// In MongoDB, update a user's roles
db.users.updateOne(
  { userName: "adminuser" },
  { $set: { roles: ["ADMIN"] } }
)
```

### 16. Admin Can Access All Users
```http
GET /user
Authorization: Basic YWRtaW51c2VyOmFkbWlucGFzcw==
```

### 17. Admin Can Access Any Journal
```http
GET /journal/anyusername
Authorization: Basic YWRtaW51c2VyOmFkbWlucGFzcw==
```

---

## 🧪 Phase 6: Error Testing

### 18. Test Invalid Login
```http
POST /public/login
Content-Type: application/json

{
  "userName": "johndoe",
  "password": "wrongpassword"
}
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Invalid username or password"
}
```

### 19. Test Duplicate User Registration
```http
POST /public/signup
Content-Type: application/json

{
  "userName": "johndoe",
  "password": "anotherpassword"
}
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Username already exists"
}
```

### 21. Create Admin User (For Testing Admin Features)
```http
POST /public/admin/signup
Content-Type: application/json

{
  "userName": "admin",
  "password": "adminpass123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Admin user registered successfully"
}
```

### 22. Admin Login
```http
POST /public/login
Content-Type: application/json

{
  "userName": "admin",
  "password": "adminpass123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "user": {
    "id": "507f1f77bcf86cd799439013",
    "userName": "admin",
    "roles": ["ADMIN"]
  }
}
```

---

## 👑 Phase 6: Admin Operations (Admin Role Required)

### 23. Get All Users (Admin Only)
```http
GET /admin/users
Authorization: Basic YWRtaW46YWRtaW5wYXNzMTIz
```

**Expected Response:** Array of all users in the system

### 24. Get User Count (Admin Only)
```http
GET /admin/users/count
Authorization: Basic YWRtaW46YWRtaW5wYXNzMTIz
```

**Expected Response:**
```json
"Total users: 2"
```

### 25. Get Specific User by ID (Admin Only)
```http
GET /admin/users/{userId}
Authorization: Basic YWRtaW46YWRtaW5wYXNzMTIz
```

### 26. Update User Role (Admin Only)
```http
PUT /admin/users/{userId}/role
Authorization: Basic YWRtaW46YWRtaW5wYXNzMTIz
Content-Type: application/json

{
  "roles": ["ADMIN"]
}
```

**Expected Response:**
```json
"User role updated successfully"
```

### 27. Update User Details (Admin Only)
```http
PUT /admin/users/{userId}
Authorization: Basic YWRtaW46YWRtaW5wYXNzMTIz
Content-Type: application/json

{
  "userName": "updateduser",
  "password": "newpassword123"
}
```

### 28. Delete User (Admin Only)
```http
DELETE /admin/users/{userId}
Authorization: Basic YWRtaW46YWRtaW5wYXNzMTIz
```

**Expected Response:**
```json
"User deleted successfully"
```

---

## 🛡️ Phase 7: Authorization Testing

### 29. Try Admin Endpoints Without Admin Role (Should Fail)
```http
GET /admin/users
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
```

**Expected Response:** `403 Forbidden`

### 30. Try Admin Endpoints Without Authentication (Should Fail)
```http
GET /admin/users
```

**Expected Response:** `401 Unauthorized`

### Public Endpoints (No Authentication)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/public/health` | Health check |
| GET | `/health-check` | Simple health check |
| POST | `/public/signup` | User registration |
| POST | `/public/admin/signup` | Admin user registration (for testing) |
| POST | `/public/login` | User login |
| POST | `/public/logout` | User logout |

### User Management (Authentication Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/user` | Get all users (Admin only) |
| GET | `/user/profile` | Get current user profile |
| GET | `/user/id/{id}` | Get user by ID (Admin only) |
| PUT | `/user/profile` | Update current user profile |
| PUT | `/user/id/{id}` | Update user by ID (Admin only) |
| DELETE | `/user/profile` | Delete current user account |
| DELETE | `/user/id/{id}` | Delete user by ID (Admin only) |

### Journal Management (Authentication Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/journal/{userName}` | Get all journal entries for user |
| POST | `/journal/{userName}` | Create new journal entry |
| GET | `/journal/{userName}/id/{id}` | Get specific journal entry |
| PUT | `/journal/{userName}/id/{id}` | Update journal entry |
| DELETE | `/journal/{userName}/id/{id}` | Delete journal entry |

### Admin Management (Admin Role Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/users` | Get all users |
| GET | `/admin/users/count` | Get total user count |
| GET | `/admin/users/{id}` | Get user by ID |
| PUT | `/admin/users/{id}` | Update user details |
| PUT | `/admin/users/{id}/role` | Update user roles |
| DELETE | `/admin/users/{id}` | Delete user |

## 🔧 Postman Setup & Testing Guide

### Setting Up Postman

1. **Download and Install Postman** from [postman.com](https://www.postman.com/downloads/)

2. **Create a New Collection**:
   - Click "New" → "Collection"
   - Name it "Journal App API"
   - Add a description: "Testing the Journal App REST API"

3. **Set Base URL Variable**:
   - In your collection, go to "Variables" tab
   - Add variable: `base_url` with value `http://localhost:8080`

### Authentication Setup in Postman

#### Method 1: Basic Auth (Recommended)
For each authenticated request:
1. Go to the "Authorization" tab in your request
2. Select "Basic Auth" from the dropdown
3. Enter:
   - Username: `johndoe` (or your registered username)
   - Password: `securepassword123` (or your password)

#### Method 2: Authorization Header
Add this header manually to authenticated requests:
```
Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
```
*(This is base64 encoded "johndoe:securepassword123")*

#### Method 3: Environment Variables (Advanced)
1. Create environment variables:
   - `username`: `johndoe`
   - `password`: `securepassword123`
   - `user_id`: (save from registration response)
   - `auth_header`: `Basic {{username}}:{{password}}` (base64 encoded)

2. In request headers, use `{{auth_header}}`

### Testing Workflow in Postman

#### Step 1: Health Check (No Auth)
- **Method**: GET
- **URL**: `{{base_url}}/public/health`
- **Expected**: 200 OK with JSON response

#### Step 2: Register User (No Auth)
- **Method**: POST
- **URL**: `{{base_url}}/public/signup`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
  "userName": "johndoe",
  "password": "securepassword123"
}
```
- **Expected**: 201 Created

#### Step 3: Login (No Auth)
- **Method**: POST
- **URL**: `{{base_url}}/public/login`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "userName": "johndoe",
  "password": "securepassword123"
}
```
- **Expected**: 200 OK with user details
- **Save user ID** from response for later use

#### Step 4: Get Profile (Auth Required)
- **Method**: GET
- **URL**: `{{base_url}}/user/profile`
- **Authorization**: Basic Auth (username/password)
- **Expected**: 200 OK with user profile

#### Step 5: Create Journal Entry (Auth Required)
- **Method**: POST
- **URL**: `{{base_url}}/journal/johndoe`
- **Authorization**: Basic Auth
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "title": "My First Entry",
  "content": "Today I learned about APIs!"
}
```
- **Expected**: 201 Created with entry details

#### Step 6: Get All Entries (Auth Required)
- **Method**: GET
- **URL**: `{{base_url}}/journal/johndoe`
- **Authorization**: Basic Auth
- **Expected**: 200 OK with array of entries

#### Step 7: Update Entry (Auth Required)
- **Method**: PUT
- **URL**: `{{base_url}}/journal/johndoe/id/{entryId}`
- **Authorization**: Basic Auth
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "title": "Updated Title",
  "content": "Updated content"
}
```
- **Expected**: 200 OK

#### Step 8: Delete Entry (Auth Required)
- **Method**: DELETE
- **URL**: `{{base_url}}/journal/johndoe/id/{entryId}`
- **Authorization**: Basic Auth
- **Expected**: 200 OK

#### Step 9: Test Authorization (Try accessing another user's data)
- **Method**: GET
- **URL**: `{{base_url}}/journal/otheruser`
- **Authorization**: Basic Auth
- **Expected**: 403 Forbidden or 404 Not Found

#### Step 10: Admin Operations (If you have admin user)
- First create admin user via `/public/admin/signup`
- Then use admin credentials for `/admin/*` endpoints

### Common Postman Tips

1. **Save Responses**: Use Postman's "Save Response" to document expected outputs
2. **Tests Tab**: Add JavaScript tests to validate responses automatically
3. **Environment Variables**: Store dynamic values like user IDs and tokens
4. **Runner**: Use Collection Runner to execute all requests in sequence
5. **Import/Export**: Share your collection with team members

### Troubleshooting in Postman

- **401 Unauthorized**: Check username/password in Basic Auth
- **403 Forbidden**: You're accessing resources you don't own (or missing admin role)
- **404 Not Found**: Wrong URL or resource doesn't exist
- **409 Conflict**: Username already exists during registration
- **500 Internal Server Error**: Check server logs, database connection

---

## 🐛 Troubleshooting

### Common Issues:

1. **401 Unauthorized**: Check your Basic Auth credentials
2. **403 Forbidden**: You're trying to access another user's resources
3. **404 Not Found**: User or journal entry doesn't exist
4. **409 Conflict**: Username already exists during registration

### Database Check:
```bash
# Connect to MongoDB
mongosh

# Check users collection
db.users.find()

# Check journal entries (embedded in users)
db.users.find({}, {journalEntries: 1})
```

---

## 🎯 Success Criteria

✅ **Complete all 30 test steps successfully**
✅ **All authentication works correctly**
✅ **Role-based authorization prevents unauthorized access**
✅ **CRUD operations work for journal entries**
✅ **Admin operations work correctly**
✅ **Error handling works as expected**

**Congratulations!** 🎉 You've successfully tested the complete Journal App API with Role-Based Authorization!</content>
<parameter name="filePath">/Users/shoebiqbal/Downloads/JournalAPP/README.md