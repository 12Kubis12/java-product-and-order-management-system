# Java Product and Order Management System

This is a Spring Boot application that provides a RESTful API for managing products and orders. It allows the creation, update, and deletion of products and orders, including stock management and order payment features.

---

## 🌐 Live Demo

You can access the deployed API via Swagger UI here:  
👉 [Live API on Railway](https://java-product-and-order-system-production-9dc7.up.railway.app/swagger-ui/index.html#/)

---

## 📦 Features

- 🛍️ **Product Management**
  - Create, update, and delete products
  - Increase product stock

- 📑 **Order Management**
  - Create and delete orders
  - Add products to orders (only if in stock)
  - Pay for an order (locks further modification)

---

## 🛠️ Technologies Used

- Java 17
- Spring Boot
- Maven
- REST API
- JDBC
- JPA
- H2 Database (in-memory)

---

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/12Kubis12/java-product-and-order-system.git
   cd java-product-and-order-system

2. Build and run the application.

3. Access the API documentation and H2 database by opening your browser and navigating to:

   - [API Documentation (Swagger UI)](http://localhost:8080/swagger-ui/index.html#/)
   - [H2 Database Console](http://localhost:8080/h2-console)  
