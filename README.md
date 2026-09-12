# webdev2 — Week 2 (Spring Boot)

This branch (`week2`) continues the Week 1 `webdev2` project, converting it into a Spring Boot application that demonstrates Spring's IoC container, dependency injection, and externalized configuration.

## How to Build

From the project root:

```
.\mvnw.cmd clean package
```

## How to Run

```
.\mvnw.cmd spring-boot:run
```

## Port

The application runs on **port 8082** (configured in `src/main/resources/application.properties` via `server.port=8082`).

## Task Summary

**Task 1 — Bootstrap the Project**
Generated a new Spring Boot project (Maven, Java 17, Spring Web) via Spring Initializr and replaced the Week 1 project files while keeping the existing `.git` history. Verified the application builds and starts successfully on port 8082.

**Task 2 — Service & Repository Beans**
Created a `Product` class (`name`, `price`) with a constructor and getters. Created `ProductRepository`, annotated with `@Repository`, holding an in-memory `List<Product>` of sample items (Laptop, Mouse, Keyboard, Monitor, Headset, Printer). Created `ProductService`, annotated with `@Service`, with a method that returns products priced above a given threshold.

**Task 3 — Constructor Dependency Injection**
`ProductService` receives its `ProductRepository` (and `ShopProperties`) through constructor injection rather than instantiating them itself. Spring's IoC container creates and wires both beans automatically.

**Task 4 — Externalized Configuration**
Added `shop.name` and `shop.currency` to `application.properties`. Created `ShopProperties`, annotated with `@Component` and `@ConfigurationProperties(prefix = "shop")`, to bind these values. `ShopProperties` is injected into `ProductService` so the shop name and currency are no longer hardcoded.

**Task 5 — Startup Wiring Report**
Created `AppRunner`, a `CommandLineRunner` bean that runs automatically at startup. It uses the Spring-managed `ProductService` to print the shop name, currency, and all products priced above PHP 5000, confirming that the repository, configuration, service, and runner beans are all correctly wired by Spring.

## Sample Output (Task 5 Startup Report)

```
================================
       PRODUCT REPORT
================================
Shop: My Web Store
Currency: PHP

Products above PHP 5000:

Laptop - PHP 45000
Monitor - PHP 12000
Printer - PHP 8500
================================
```

## Project Structure

```
webdev2
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    └── main
        ├── java
        │   └── com/stephanie/webdev2
        │       ├── Webdev2Application.java
        │       ├── Product.java
        │       ├── ProductRepository.java
        │       ├── ProductService.java
        │       ├── ShopProperties.java
        │       └── AppRunner.java
        └── resources
            └── application.properties
```
