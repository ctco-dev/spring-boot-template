# Spring Boot Teaching Template for Frontend Developers

Welcome, developer! 👋 This project is a **learning platform** for frontend devs who want to understand the Java + Spring ecosystem — through code, not slides.

## ✅ What you'll find

- ✅ A working feature: **Todo**
    - `TodoController`, `TodoService`, `TodoRepository`, `Todo` model
    - Uses MongoDB for persistence with Testcontainers for testing
    - Demonstrates Dependency Injection (DI) using constructor injection
    - Has Swagger UI docs via springdoc-openapi

- 🧠 Learning-oriented JavaDocs with links to Spring resources
- 🧪 **ArchUnit tests** to enforce clean architecture
- 🔍 Functional tests to validate feature behavior using Testcontainers

## 🏗 Architecture

This project implements **Vertical Slice Architecture (VSA)**, organizing code around features rather than technical layers. You can explore this through the **Todo** feature implementation.

### Why VSA?

- 🎯 Feature-focused organization
- 🔄 Independent, full-stack slices
- 🚀 Faster development cycles
- 🛠 Easier maintenance

Learn more about VSA:
- [Baeldung: Vertical Slice Architecture in Java](https://www.baeldung.com/java-vertical-slice-architecture)
- [Exploring Software Architecture: Vertical Slice](https://medium.com/@andrew.macconnell/exploring-software-architecture-vertical-slice-789fa0a09be6)

### 🎯 Todo Feature Showcase

The **Todo** feature demonstrates VSA principles in action:
- Complete vertical slice from API to persistence
- Self-contained in `features/todo` package
- Includes dedicated tests and documentation
- Shows proper dependency management

## 🚀 Run the app

1. Make sure you have:
    - Java 21
    - Docker running

2. Start the app:
```bash
./gradlew bootRun
```
Open Swagger docs: http://localhost:8080/swagger-ui.html

## 📚 Learning Tasks

### Task 1: Create the "Greeting" Feature

Your task is to build a new Spring feature called `greeting`, and connect it to the existing `todo` feature.

---

### 🎯 Objective

Create a REST endpoint `/api/greeting` that returns a message like: "Hello from Spring! You have 3 open tasks."


This feature should use the existing `TodoService` via constructor-based Dependency Injection.

---

### 🛠 Steps

1. **Create a new package**:  
   `lv.ctco.springboottemplate.features.greeting`

2. **Implement `GreetingService`**
  - Inject `TodoService` using constructor-based DI
  - Use `todoService.getAllTodos()` and count how many are not completed
  - Return a string like: `"Hello from Spring! You have X open tasks."`

3. **Create `GreetingController`**
  - Map it to `/api/greeting`
  - Delegate to `GreetingService`

4. **Follow naming conventions**
  - Class names must end with `Service`, `Controller`, etc.
  - Use annotations like `@Service`, `@RestController`, `@RequestMapping`

---

### 🧪 Test Yourself

We've prepared tests to verify your work:

✅ ArchUnit tests will prevent you from structure issues

✅ `GreetingControllerFunctionalTest.java`  
📍 Location: `src/test/java/lv/ctco/springboottemplate/features/greeting/`  
🔒 Also `@Disabled`. Remove to test your endpoint.

> If All tests pass and the greeting appears at `/api/greeting`, you've completed the task!

---

### 💡 Hints

- Constructor injection is preferred over `@Autowired`
- Use Java Streams to filter open todos
- The `TodoService` is already a Spring bean — you can inject it!

---

### ✅ Done?

When both tests are green and the endpoint works, **congratulations!**  
You've created a feature using Spring idioms and learned how features interact via Dependency Injection.

🔥 Welcome to the backend side.

### Task 2: Create the "Statistics" Feature

#### 🎯 Objective
Create a REST endpoint `/api/statistics` that provides insights about Todo items.

#### 📋 Requirements

**1. API Endpoint**
- Base path: `/api/statistics`
- Support query parameters:
    - `from` - Start date (e.g., `2023-01-01`)
    - `to` - End date (e.g., `2023-12-31`)
    - `format` - Response format (`summary` or `detailed`)

**2. Statistics to Include**
- Total number of todos
- Number of completed todos
- Number of pending todos
- Todos per user breakdown

**3. Technical Requirements**
- Use MongoDB aggregations for efficient data processing
- Create DTOs for API responses
- Implement proper error handling
- Follow existing architectural patterns

**4. Sample Response**
```json
{
  "totalTodos": 10,
  "completedTodos": 7,
  "pendingTodos": 3,
  "userStats": {
    "user1": 5,
    "user2": 3,
    "user3": 2
  }
}
```
