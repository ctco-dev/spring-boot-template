# Spring Boot Teaching Template for Frontend Developers

Welcome, developer! 👋 This project is a **learning platform** for frontend devs who want to understand the Java + Spring
ecosystem — through code, not slides.

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

This project implements **Vertical Slice Architecture (VSA)**, organizing code around features rather than technical
layers. You can explore this through the **Todo** feature implementation.

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


2. Start MongoDB:

```bash
docker run -d --name mongodb -p 27017:27017 mongo:latest
```

3. Start the app:

```bash
./gradlew bootRun
```

Open Swagger docs: http://localhost:8080/swagger-ui.html

## 📚 Learning Tasks

### 🌳 Branch Management

1. **Personal Feature Branch**
   - You should have a personal feature branch created by trainers
   - This will be your main working branch

2. **Task Branches**
   - For each task, create a new branch from your personal feature branch
   - Naming convention: `task/<task-number>` (e.g., `task/1`, `task/2`)

3. **Pull Request Process**
   - Create a PR from your task branch to your personal feature branch
   - Assign the following reviewers to your PR:
     - Oleg
     - Alexey
     - Igor

### 📝 Available Tasks

1. [Task 1: Create the "Greeting" Feature](TASK1.md)
   - Implement a new greeting endpoint
   - Connect it with the existing Todo feature
   
2. [Task 2: Create the "Statistics" Feature](TASK2.md)
   - Build statistics endpoint for Todo items
   - Implement date filtering and different response formats

For detailed requirements and step-by-step instructions, please refer to the respective task files: [TASK1.md](TASK1.md) and [TASK2.md](TASK2.md)

