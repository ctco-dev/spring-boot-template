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

    * either from, to, or both can pe provided

**2. Statistics to Include**

- Total number of todos
- Number of completed todos
- Number of pending todos
- Todos per user breakdown

**3. Technical Requirements**

- Use MongoDB aggregations for efficient data
  processing (https://www.mongodb.com/resources/products/capabilities/aggregation-pipeline)
    - Minimize amount of queries to reduce chatiness! (https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing)
- Create DTOs for API responses
- Handle input errors on the controller level
    - Handle HTTP status codes with appropriate error responses (400 Bad Request for invalid parameters, 200 OK for
      success, 500 Internal Server Error for server errors)
- Follow project architecture
- Controller integration test and Repository integration test
- Must use test containers, not mocks!

**4. Summary Sample Response**

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

**5. Detailed Sample Response**

```json
{
  "totalTodos": 10,
  "completedTodos": 7,
  "pendingTodos": 3,
  "userStats": {
    "user1": 5,
    "user2": 3,
    "user3": 2
  },
  "todos": {
    "completed": [
      {
        "id": "1",
        "title": "Deploy to production",
        "createdBy": "user1",
        "createdAt": "2024-03-15T10:30:00Z",
        "completedAt": "2024-03-15T14:20:00Z"
      }
      // ... more completed todos
    ],
    "pending": [
      {
        "id": "8",
        "title": "Review pull request",
        "createdBy": "user2",
        "createdAt": "2024-03-15T09:00:00Z"
      }
      // ... more pending todos
    ]
  }
}
```