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
| POST | `/public/signup` | User registration |
| POST | `/public/login` | User login |
| POST | `/public/logout` | User logout |

### User Management (Authentication Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/user/profile` | Get current user profile |
| PUT | `/user/profile` | Update current user profile |
| DELETE | `/user/profile` | Delete current user account |
| GET | `/user` | Get all users (Admin only) |
| GET | `/user/id/{id}` | Get user by ID (Admin only) |
| PUT | `/user/id/{id}` | Update user by ID (Admin only) |
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

### Special Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/public/admin/signup` | Create admin user (for testing) |

## 🔧 Postman Setup

### Authentication Setup
1. For authenticated requests, use **Basic Auth** in Postman:
   - Username: `johndoe`
   - Password: `securepassword123`

2. Or use **Authorization Header**:
   ```
   Authorization: Basic am9obmRvZTpzZWN1cmVwYXNzd29yZDEyMw==
   ```

### Environment Variables
Create these variables in Postman:
- `base_url`: `http://localhost:8080`
- `username`: `johndoe`
- `password`: `securepassword123`
- `user_id`: (save from registration response)

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