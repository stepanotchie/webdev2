# Task Tracker (Spring Boot + Thymeleaf)

Run: `mvn spring-boot:run`, then open http://localhost:8080/tasks
Test: `mvn test`

| Route | Purpose |
|---|---|
| GET /tasks | list (empty-state message when none) |
| GET /tasks/{id} | detail, or friendly 404 page via TaskNotFoundException |
| GET /tasks/new | empty validated form |
| POST /tasks | validate -> save -> redirect:/tasks |

Layers: controller (HTTP/views) -> service (business; repository constructor-injected) -> repository (in-memory list + AtomicLong id counter) -> model (Task + validation).
