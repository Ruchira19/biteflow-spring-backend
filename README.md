# BiteFlow Spring Backend

A comprehensive REST API backend for a food ordering platform built using Spring Boot, Spring Security, JWT authentication, MySQL, and JPA/Hibernate.

This backend provides secure authentication, role-based authorization, shopping cart management, order processing, payment tracking, inventory synchronization, and category management for a complete food ordering workflow.

---

# Features

## Authentication & Security

- JWT-based stateless authentication
- Spring Security integration
- Role-based authorization using ADMIN and CUSTOMER roles
- Secure password encryption / Custom JWT authentication filter
- Method-level authorization

## User Management

- User registration and login
- Admin user creation
- User profile retrieval
- User deletion with admin protection rules
- Automatic shopping cart creation for new users

## Food & Category Management

- Food category management
- Food item CRUD operations
- Stock quantity management
- Automatic availability synchronization
- Public food browsing endpoints

## Shopping Cart

- Add items to cart
- Update cart item quantities
- Remove items from cart
- Clear shopping cart
- Real-time stock validation

## Order Management

- Place food orders
- View customer orders
- View all orders (ADMIN)
- Update order statuses
- Cancel orders
- Automatic inventory restoration on cancellation

## Payment Management

- Payment tracking per order
- Payment status updates
- Transaction reference management
- 
## Database & Persistence

- MySQL relational database
- JPA/Hibernate ORM
- Transaction management
- Foreign key relationships
- Database schema initialization using schema.sql
- Sample data loading using data.sql

## Architecture
- Layered architecture
- Service-oriented business logic

## System Architecture Layers

- controller - REST API endpoints
- service - Business logic implementation
- repository - Database access layer
- entity - JPA entities and enums
- dto - Request/response data transfer objects
- security - JWT and authentication components
- exception - Global exception handling
- config - Application and security configura


## Database Schema

- The application automatically initializes the database using: schema.sql & data.sql

## Main Tables
- users
- category
- food_item
- cart
- cart_item
- orders
- order_item
- payment


## Entity Relationships
  - User -> One Cart
  - User -> Many Orders
  - Cart -> Many CartItems
  - Category -> Many FoodItems 
  - Order -> Many OrderItems 
  - Order -> One Payment

## Default Sample Data

- The application automatically inserts:
  - Default admin account 
  - Default customer account 
  - Food categories 
  - Food items 
  - Shopping carts

## Default Admin Account
    Email    : admin@biteflow.com
    Password : admin123
    Role     : ADMIN

## Default Customer Account
    Email    : customer@biteflow.com 
    Password : customer123 
    Role     : CUSTOMER
---

# API Documentation

    Postman Collection: https://ruchira-5268094.postman.co/workspace/projects~79065c53-1c39-41a8-bdd4-ea7c676f395c/collection/45687506-90350511-14b6-420c-96e6-fac71534730f?action=share&source=copy-link&creator=45687506
    
    or
    
    Refer to the API_DOC PDF file in the resources folder for more details.

# Error Response Format

    {
    "timestamp": "2026-05-09T10:30:45",
    "status": 400,
    "error": "Bad Request",
    "message": "Email is already registered",
    "path": "/api/v1/auth/signup"
    }

# HTTP Status Codes
    Status Meaning
    200	Successful request
    201	Resource created
    204	Resource deleted
    400	Validation or business rule failure
    401	Unauthorized
    403	Forbidden
    404	Resource not found

# Technology Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 25 | Programming Language |
| Spring Boot | 4.0.6 | Backend Framework |
| Spring Security | Latest | Authentication & Authorization |
| Spring Data JPA | Latest | ORM & Data Access |
| MySQL | 8.0+ | Relational Database |
| JWT (jjwt) | 0.12.x | Token-Based Authentication |
| ModelMapper | 3.2.4 | Object Mapping |
| Lombok | Latest | Boilerplate Reduction |
| Maven | Latest | Build Tool |

---

# Setup Instructions

## Clone Repository

git clone https://github.com/Ruchira19/biteflow-spring-backend.git

## Configure Database

    CREATE DATABASE food_ordering_db;

Update datasource properties inside:
src/main/resources/application-dev.properties

---

# Business Rules

- Empty carts cannot place orders
- Stock quantities are validated before ordering
- Delivered orders cannot be modified
- Cancelled orders restore inventory
- Payments initialize as PENDING
- Customers can only access their own resources
- At least one ADMIN account must remain

---

# Author

Ruchira Sampath

GitHub:
https://github.com/Ruchira19
