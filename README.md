# Java Product and Order Management System 🛒

This is a Spring Boot application that provides a RESTful API for managing products and orders. It allows the creation, update, and deletion of products and orders, including stock management and order payment features.

---

## ✨ Features

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
- H2 Database (in-memory)
- JDBC and JPA -> you can switch between them using the `spring.profiles.default` property in this file:

  [`src/main/resources/application.properties`](https://github.com/12Kubis12/java-product-and-order-system/blob/main/src/main/resources/application.properties)
    ```properties
    spring.profiles.default=jpa  # Use JPA
    spring.profiles.default=jdbc # Use JDBC
    ```

---

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/12Kubis12/java-product-and-order-system.git

2. Build and run the application.

3. Access the API documentation and H2 database by opening your browser and navigating to:

   - [API Documentation (Swagger UI)](http://localhost:8080/swagger-ui/index.html#/)
   - [H2 Database Console](http://localhost:8080/h2-console)

4. Testing:
   - The [`src/main/resources/data.sql`](https://github.com/12Kubis12/java-product-and-order-system/blob/main/src/main/resources/data.sql) file provides predefined data for the database tables, allowing you to manually test the application with sample products and orders.
   - To prevent conflicts during integration tests, comment out all but the first four lines in `data.sql` before running them.

---

## 📚 References

This project is based on an assignment from the [Street of Code Java Spring Boot Course](https://github.com/StreetOfCode/java-kurz-spring-boot-zadanie).
