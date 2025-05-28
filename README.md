# Spring Boot Teaching Template for Frontend Developers

Welcome, developer! 👋 This project is a **learning platform** for frontend devs who want to understand the Java + Spring ecosystem — through code, not slides.

## ✅ What you’ll find

- ✅ A working feature: **Todo**
  - `TodoController`, `TodoService`, `TodoRepository`, `Todo` model
  - Uses MongoDB for persistence
  - Demonstrates Dependency Injection (DI) using constructor injection
  - Has Swagger UI docs via springdoc-openapi

- 🧠 Learning-oriented JavaDocs with links to Spring resources
- 🧪 **ArchUnit tests** to enforce clean architecture
- 🔍 Functional tests to validate feature behavior

---

## 🚀 Run the app

1. Make sure you have Java 21 and Docker installed
2. Run MongoDB with Docker:

```bash
docker run --rm -d -p 27017:27017 --name mongo mongo:6

```
Start the app
./gradlew bootRun

Open Swagger docs: http://localhost:8080/swagger-ui.html

## 🧩 Your Mission: Create the `greeting` Feature

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
