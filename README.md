# Rule Engine with AST

This project is a simple 3-tier rule engine application that determines user eligibility based on attributes like age, department, income, spend, etc. It uses an Abstract Syntax Tree (AST) to represent conditional rules and allows for dynamic creation, combination, and modification of these rules.

## Features
- Dynamic rule creation and evaluation using Abstract Syntax Tree (AST)
- Backend support with a database for rule storage and metadata
- Simple UI for rule creation and management

---

## Table of Contents
1. [Requirements](#requirements)
2. [Installation](#installation)
3. [Running the Application](#running-the-application)
4. [API Endpoints](#api-endpoints)
5. [Example Use Cases](#example-use-cases)
6. [Contributing](#contributing)

---

## Requirements

Before you begin, ensure you have the following installed:
- **Java** (version 17.0.12)
- **Spring Boot** (version 3.x)
- **MySQL**
- **Maven** (version 3.x)

---

## Installation

### Step 1: Clone the Repository

```bash
git clone https://github.com/nitishparimi/RuleEngineAST.git
```

### Step 2: Setup the Database
1. Install MySQL
2. Create a new database:
   ```sql
   CREATE DATABASE ast_db;
   ```

### Step 3: Configure Database Connection
1. In `src/main/resources/application.properties`, set your database configurations:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ast_db
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   spring.jpa.hibernate.ddl-auto=update
   ```

### Step 4: Build and Run the Application
1. Build the application using Maven:
   ```bash
   mvn clean install
   ```
2. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

---

## Running the Application

The application will be accessible at:
```
http://localhost:9000/create
```

### API Endpoints

1. **Create Rule**: POST `/create`
   - Request Body: `rule_string`
   - Example: `"((age > 30 AND department = 'Sales') OR (age < 25 AND department = 'Marketing')) AND (salary > 50000 OR experience > 5)"`

2. **Combine Rules**: POST `/mergeRules`
   - Request Body: List of rule strings to combine
   - Example: `["rule1", "rule2"]`

3. **Evaluate Rule**: POST `/evaluate`
   - Request Body: `{"age": 35, "department": "Sales", "salary": 60000, "experience": 3}`

---

## Example Use Cases

### Example 1: Create Rule
```json
POST /create
{
  "rule_string": "((age > 30 AND department = 'Sales') OR (age < 25 AND department = 'Marketing')) AND (salary > 50000 OR experience > 5)"
}
```

### Example 2: Combine Rules
```json
POST /mergeRules
{
  "rules": ["rule1", "rule2"]
}
```

### Example 3: Evaluate Rule
```json
POST /evaluate
{
  "data": {
    "age": 35,
    "department": "Sales",
    "salary": 60000,
    "experience": 3
  }
}
```



## Contributing

Contributions are welcome! Please fork the repository, make your changes, and submit a pull request.

For major changes, please open an issue first to discuss what you would like to change.

---


### Happy Coding! 😄

---
