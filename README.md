# 🗳️ EVoting System - Spring Boot REST API

A backend REST API for an **Electronic Voting System** built with Spring Boot, JPA, and MySQL. It supports user registration, election management, casting votes, and retrieving election results.

---

## 🚀 Tech Stack

| Technology       | Version |
|-----------------|---------|
| Java            | 17      |
| Spring Boot     | 3.0.0   |
| MySQL           | 8+      |

---

## 📁 Project Structure

```
src/main/java/com/codingninjas/EVotingSystem/
├── controllers/
│   └── EVotingController.java     # REST endpoints
├── services/
│   └── EVotingService.java        # Business logic
├── repositories/
│   ├── UserRepository.java
│   ├── ElectionRepository.java
│   ├── ElectionChoiceRepository.java
│   └── VoteRepository.java
├── entities/
│   ├── User.java
│   ├── Election.java
│   ├── ElectionChoice.java
│   └── Vote.java
└── EVotingApplication.java        # Main entry point
```

---

## ⚙️ Setup & Configuration

### Prerequisites
- Java 17+
- MySQL 8+

### 1. Clone the repository
```bash
git clone https://github.com/Ankitsalvgupta/EVoting-System-SpringBoot.git
cd EVoting-System-SpringBoot
```

### 2. Configure the database

Open `src/main/resources/application.yml` and update your MySQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/evoting?createDatabaseIfNotExist=true
    username: your_mysql_username
    password: your_mysql_password
```

> The database `evoting` will be created automatically on first run.

### 3. Run the application
```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`

---

## 📡 API Endpoints

### 👤 User

| Method | Endpoint     | Description          |
|--------|-------------|----------------------|
| POST   | `/add/user`  | Register a new voter |
| GET    | `/get/users` | Get all users        |

**POST `/add/user`** — Request Body:
```json
{
  "name": "Ankit"
}
```

---

### 🗳️ Election

| Method | Endpoint          | Description           |
|--------|------------------|-----------------------|
| POST   | `/add/election`   | Create a new election |
| GET    | `/get/elections`  | Get all elections     |

**POST `/add/election`** — Request Body:
```json
{
  "name": "Presidential Election 2025"
}
```

---

### 🧑‍💼 Election Choice (Candidates)

| Method | Endpoint               | Description                           |
|--------|------------------------|---------------------------------------|
| POST   | `/add/electionChoice`  | Add a candidate to an election        |
| GET    | `/get/electionChoices` | Get all election choices              |
| GET    | `/count/{electionId}`  | Count choices for a specific election |

**POST `/add/electionChoice`** — Request Body:
```json
{
  "name": "Candidate A",
  "election": { "id": 1 }
}
```

---

### 📮 Vote

| Method | Endpoint     | Description   |
|--------|-------------|---------------|
| POST   | `/add/vote`  | Cast a vote   |
| GET    | `/get/votes` | Get all votes |

**POST `/add/vote`** — Query Parameters:
```
/add/vote?userId=1&electionId=1&electionChoiceId=2
```
> ⚠️ A user can only vote **once per election**. Duplicate votes throw an error.

---

### 📊 Results

| Method | Endpoint                          | Description                        |
|--------|----------------------------------|------------------------------------|
| GET    | `/count/votes`                    | Total votes across all elections   |
| GET    | `/count/votes/{electionName}`     | Total votes in a specific election |
| GET    | `/winner/election/{electionName}` | Winner of a specific election      |

---

## 🛡️ Business Rules

- A **user cannot vote twice** in the same election.
- Each **ElectionChoice must be linked** to an existing election.
- The **winner** is determined by the highest vote count for a choice in an election.

---

## 👨‍💻 Author

**Ankit Gupta**  
GitHub: [@Ankitsalvgupta](https://github.com/Ankitsalvgupta)
