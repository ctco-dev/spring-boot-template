# Project Setup
* List of recommended IDEs with links
  * Basic intro to Intellij interface
* Docker compose to run mongodb w or w/o application (personal preference)
* JDK setup
  * Download link to v21
  * Instructions how to install (optional)
  * Instructions how to change JDK version in Intellij IDEA
* How to run application in IDEA

# Faced errors
  * Java version mismatch:
    ```
    Execution failed for task ':compileJava'.
    > error: invalid source release: 21`
    ```

# Overall Documentation
* Visualization for Controller -> Service -> Repository principle
* Basic MongoDB information (optional)
  * SQL vs noSQL
  * Examples of find and insert queries
  * Should mention that Spring automajically creates MongoDB queries under the hood

# Additional topics
* Application configuration (`application.ymal`)

# Testing
* Should add command how to run tests
* Some imports are available in multiple packages and users won't know the difference between `org.testcontainers.junit.jupiter.Container` and `net.bytebuddy.utility.dispatcher.JavaDispatcher.Container`
* Provide examples of unit tests (currently only high level integration tests are available)
* Could provide TDD like unit test file for first task were users could incrementally (by uncommenting code) implement part of service 

# Task description
## Task #1
* Should specify that `GreetingService` should implement `greet` method as it is used in test assertions
* `Use todoService.getAllTodos() and count how many are not completed` better to mark `NOT COMPLETED`, so users will pay attention
* `Also @Disabled. Remove to test your endpoint.` unclear wording