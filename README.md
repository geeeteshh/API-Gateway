# Spring Boot Microservices: API Gateway & JWT Security (RBAC)

A secure, production-ready microservices architecture built with Spring Boot, Spring Cloud Gateway, and JSON Web Tokens (JWT) for Role-Based Access Control (RBAC).

---

## 🏗️ Architecture Overview

```text
               +---------------------------------------+
               |             Client (Postman)          |
               +---------------------------------------+
                                   |
                                   v  (Port 8082)
               +---------------------------------------+
               |              API Gateway              |
               |      (Spring Cloud Gateway + JWT)      |
               +---------------------------------------+
                 /                                   \
  (Routes to Auth)                               (Routes to Products + Role Check)
               /                                       \
              v  (Port 8080)                            v  (Port 8083)
  +-----------------------+                   +-----------------------+
  |     Auth Service      |                   |    Product Service    |
  | (Generates JWT Token) |                   |  (Resource Microservice)|
  +-----------------------+                   +-----------------------+
```

1. **Authentication:** The client sends credentials to the Auth Service via the Gateway. On success, the client receives a JWT containing their roles.
2. **Access Control:** All subsequent requests are routed through the Gateway. The Gateway interceptor decodes the JWT using the shared secret, verifies its signature, checks if the user has the required roles for the endpoint, and mutates the request headers to forward the roles downstream.

---

## 🛠️ Microservices breakdown

### 1. `api-gateway` (Port `8082`)
* Handles central routing, token verification, and role authorization.
* **Key Files:** 
  * [GatewayConfig.java](api-gateway/src/main/java/com/example/api_gateway/config/GatewayConfig.java) (defines programmatic routes and checks roles).
  * [JwtAuthenticationGatewayFilterFactory.java](api-gateway/src/main/java/com/example/api_gateway/filter/JwtAuthenticationGatewayFilterFactory.java) (validates tokens and extracts claims).

### 2. `secure-gateway-backend-service` (Port `8080`)
* Acts as the Identity/Authentication provider.
* Contains in-memory testing credentials:
  * **User:** `user` / `password` (has role `ROLE_USER`)
  * **Admin:** `admin` / `password` (has role `ROLE_ADMIN`)

### 3. `product-service` (Port `8083`)
* Simple resource service serving product lists.
* Exposes public, user-only, and admin-only endpoints.

---

## 🚀 How to Run the Project Locally

Run each service in a separate terminal tab from the root directory:

```bash
# 1. Start Authentication Service
cd secure-gateway-backend-service && ./mvnw spring-boot:run

# 2. Start Product Service
cd product-service && ./mvnw spring-boot:run

# 3. Start API Gateway
cd api-gateway && ./mvnw spring-boot:run
```

---

## 🧪 Testing the APIs (Postman / Curl)

All requests go through the API Gateway (port `8082`).

### 1. Public Endpoint (No Token Required)
* **Request:** `GET http://localhost:8082/products/public`
* **Response:** `200 OK` (Public catalog message)

---

### 2. Authenticating as 'User'
* **Request:** `POST http://localhost:8082/authenticate`
* **Headers:** `Content-Type: application/json`
* **Body:**
  ```json
  {
    "username": "user",
    "password": "password"
  }
  ```
* **Response:** Returns a long JWT Token string. Copy this token.

---

### 3. Accessing User-Secured Endpoints
* **Request:** `GET http://localhost:8082/products/user`
* **Headers:** `Authorization: Bearer <PASTE_USER_TOKEN_HERE>`
* **Response:** `200 OK` (User specific messages)

---

### 4. Testing Role Restriction (Accessing Admin Endpoint with User Token)
* **Request:** `GET http://localhost:8082/products/admin`
* **Headers:** `Authorization: Bearer <PASTE_USER_TOKEN_HERE>`
* **Response:** `403 Forbidden` (User does not have admin privileges)

---

### 5. Authenticating as 'Admin' and Accessing Admin Endpoints
* **Request:** `POST http://localhost:8082/authenticate`
* **Body:**
  ```json
  {
    "username": "admin",
    "password": "password"
  }
  ```
* **Get Admin Token** and query the admin endpoints:
  * `GET http://localhost:8082/products/admin`
  * `GET http://localhost:8082/products/huhh`
* **Headers:** `Authorization: Bearer <PASTE_ADMIN_TOKEN_HERE>`
* **Response:** `200 OK` (Admin catalog messages)
