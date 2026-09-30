# 🗳️ EVoting System – Spring Boot REST API

A backend REST API for an **electronic voting system**, built with Spring Boot, Spring Data JPA and MySQL. It uses **JWT authentication** with **role-based access control** (Admin / Voter) so that only admins can manage elections and only registered voters can cast a vote.

---

## ✨ Features

- User registration and login with **JWT** (stateless sessions)
- Passwords hashed with **BCrypt**
- **Role-based access**: `ADMIN` manages elections and candidates, `VOTER` casts votes
- Admin account **auto-created on first startup**
- Create elections and add candidates (election choices)
- Cast a vote — the voter is identified from the JWT, not from a request parameter
- One vote per user per election, enforced in the service layer **and** by a database unique constraint
- A vote must be for a candidate that belongs to that election (mismatches are rejected with `400`)
- Vote counts and winner lookup per election
- Request validation on register/login (`@Valid`)
- Consistent JSON error responses via a global exception handler

---

## 🚀 Tech Stack

| Technology                | Version / Notes             |
|---------------------------|-----------------------------|
| Java                      | 17                          |
| Spring Boot               | 4.1.1                       |
| Spring Web MVC            | REST controllers            |
| Spring Data JPA           | Hibernate, `ddl-auto: update` |
| Spring Security           | Stateless, JWT filter       |
| JJWT                      | 0.12.6                      |
| MySQL                     | 8+                          |
| Lombok                    | Boilerplate reduction       |
| Maven                     | via Maven Wrapper (`mvnw`)  |

---

## 📁 Project Structure

```
src/main/java/com/codingninjas/EVotingSystem/
├── config/
│   └── SecurityConfig.java            # Security filter chain, route rules, BCrypt bean
├── controller/
│   ├── AuthController.java            # /auth/register, /auth/login
│   └── EVotingController.java         # Election, candidate, vote and result endpoints
├── dto/
│   ├── RegisterRequest.java           # name + password (min 8 chars)
│   ├── LoginRequest.java
│   └── AuthResponse.java              # { "token": "..." }
├── entity/
│   ├── User.java                      # Table: users
│   ├── Role.java                      # VOTER, ADMIN
│   ├── Election.java
│   ├── ElectionChoice.java            # Candidate belonging to an election
│   └── Vote.java
├── exception/
│   ├── GlobalExceptionHandler.java    # Maps exceptions to HTTP status codes
│   ├── ErrorResponse.java             # JSON error body
│   ├── ResourceNotFoundException.java # -> 404
│   └── DuplicateVoteException.java    # -> 409
├── repository/
│   ├── UserRepository.java
│   ├── ElectionRepository.java
│   ├── ElectionChoiceRepository.java  # Includes winner query
│   └── VoteRepository.java
├── security/
│   ├── JwtService.java                # Generates / parses tokens
│   ├── JwtAuthFilter.java             # Reads the Bearer token on each request
│   ├── CustomUserDetailsService.java
│   └── AdminSeeder.java               # Creates the "admin" user on startup
├── service/
│   └── EVotingService.java            # Business logic
└── EVotingApplication.java            # Main entry point

src/main/resources/
└── application-example.yml            # Config template (copy to application.yml)
```

---

## ⚙️ Setup & Configuration

### Prerequisites

- Java 17+
- MySQL 8+
- (Maven is optional — the included `mvnw` wrapper is enough)

### 1. Clone the repository

```bash
git clone https://github.com/Ankitsalvgupta/EVoting-System-SpringBoot.git
cd EVoting-System-SpringBoot
```

### 2. Configure the application

`src/main/resources/application.yml` is **git-ignored** (it holds your local settings), so a fresh clone does not contain it. Create it from the template:

```bash
cp src/main/resources/application-example.yml src/main/resources/application.yml
```

(On Windows PowerShell: `copy src\main\resources\application-example.yml src\main\resources\application.yml`)

The template reads everything from **environment variables**, so no secrets live in the file or in Git:

| Variable         | Required | Default                                                          | Purpose                                  |
|------------------|----------|------------------------------------------------------------------|------------------------------------------|
| `DB_PASSWORD`    | Yes      | –                                                                | MySQL password                           |
| `JWT_SECRET`     | Yes      | –                                                                | Base64 key used to sign JWTs (≥ 256 bits) |
| `ADMIN_PASSWORD` | Yes      | –                                                                | Password of the seeded `admin` user      |
| `DB_URL`         | No       | `jdbc:mysql://localhost:3306/evoting?createDatabaseIfNotExist=true` | JDBC URL                                 |
| `DB_USERNAME`    | No       | `root`                                                           | MySQL username                           |

The app **will not start** if a required variable is missing. The `evoting` database is created automatically on first run.

Generate a `JWT_SECRET` with:

```bash
openssl rand -base64 32
```

Then export the variables (Linux/macOS):

```bash
export DB_PASSWORD="<your-mysql-password>"
export JWT_SECRET="<your-base64-secret>"
export ADMIN_PASSWORD="<a-strong-password>"
```

On Windows (PowerShell):

```powershell
$env:DB_PASSWORD="<your-mysql-password>"
$env:JWT_SECRET="<your-base64-secret>"
$env:ADMIN_PASSWORD="<a-strong-password>"
```

> ⚠️ Never commit real credentials. Use a different `JWT_SECRET` and `ADMIN_PASSWORD` for every environment.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

(On Windows use `mvnw.cmd spring-boot:run`. On macOS/Linux, if you get *permission denied*, run `chmod +x mvnw` first.)

The server starts at **http://localhost:8080**.

---

## 🔐 Authentication & Roles

| Role    | How you get it                                              | What it can do                                       |
|---------|-------------------------------------------------------------|------------------------------------------------------|
| `ADMIN` | Seeded automatically as user `admin` on first startup       | Create elections and candidates, list users and votes |
| `VOTER` | Anyone who registers through `/auth/register`               | Cast votes                                           |

The seeded admin logs in with username `admin` and the password set in `ADMIN_PASSWORD`.

**Flow**

1. `POST /auth/register` to create a voter account (or log in as `admin`).
2. `POST /auth/login` to receive a JWT.
3. Send the token on every protected request:

```
Authorization: Bearer <token>
```

Tokens expire after `jwt.expiration-ms` (1 hour by default). Requests with a missing, invalid or expired token get `401 Unauthorized`; a valid token without the required role gets `403 Forbidden`.

---

## 📡 API Endpoints

### Access summary

| Access                  | Endpoints                                                                                      |
|-------------------------|------------------------------------------------------------------------------------------------|
| Public                  | `/auth/**`                                                                                     |
| `ADMIN` only            | `POST /add/election`, `POST /add/electionChoice`, `GET /get/users`, `GET /get/votes`           |
| `VOTER` only            | `POST /add/vote`                                                                               |
| Any authenticated user  | `GET /get/elections`, `GET /get/electionChoices`, `GET /count/**`, `GET /winner/**`            |

### 🔑 Auth

| Method | Endpoint          | Description                        |
|--------|-------------------|------------------------------------|
| POST   | `/auth/register`  | Register a new voter               |
| POST   | `/auth/login`     | Log in and receive a JWT           |

**POST `/auth/register`**
```json
{
  "name": "ankit",
  "password": "supersecret123"
}
```
- `name` must not be blank; `password` must be at least 8 characters.
- Returns `201 Created` with `Registered`, or `409 Conflict` if the username is taken.

**POST `/auth/login`**
```json
{
  "name": "ankit",
  "password": "supersecret123"
}
```
Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 🗳️ Election *(create: Admin)*

| Method | Endpoint           | Description           |
|--------|--------------------|-----------------------|
| POST   | `/add/election`    | Create a new election |
| GET    | `/get/elections`   | List all elections    |

**POST `/add/election`**
```json
{
  "name": "Presidential Election 2026"
}
```
Election names are unique.

---

### 🧑‍💼 Election Choices / Candidates *(create: Admin)*

| Method | Endpoint                | Description                              |
|--------|-------------------------|------------------------------------------|
| POST   | `/add/electionChoice`   | Add a candidate to an existing election  |
| GET    | `/get/electionChoices`  | List all candidates                      |
| GET    | `/count/{electionId}`   | Number of candidates in an election      |

**POST `/add/electionChoice`**
```json
{
  "name": "Candidate A",
  "election": { "id": 1 }
}
```

---

### 📮 Vote *(Voter)*

| Method | Endpoint       | Access  | Description        |
|--------|----------------|---------|--------------------|
| POST   | `/add/vote`    | Voter   | Cast a vote        |
| GET    | `/get/votes`   | Admin   | List all votes     |

**POST `/add/vote`** — query parameters:
```
/add/vote?electionId=1&electionChoiceId=2
```
The voter is taken from the JWT, so no `userId` is needed. A user can vote **only once per election** (a second attempt returns `409`), and `electionChoiceId` must belong to `electionId` (otherwise `400`).

---

### 📊 Results *(any authenticated user)*

| Method | Endpoint                             | Description                          |
|--------|--------------------------------------|--------------------------------------|
| GET    | `/count/votes`                       | Total votes across all elections     |
| GET    | `/count/votes/{electionName}`        | Total votes in a specific election   |
| GET    | `/winner/election/{electionName}`    | Candidate with the most votes        |

---

## 🧪 Quick Test with cURL

```bash
# 1. Log in as admin
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"name":"admin","password":"<ADMIN_PASSWORD>"}'

# 2. Create an election (admin token)
curl -X POST http://localhost:8080/add/election \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Class Monitor 2026"}'

# 3. Add a candidate
curl -X POST http://localhost:8080/add/electionChoice \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Candidate A","election":{"id":1}}'

# 4. Register and log in as a voter
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"ankit","password":"supersecret123"}'

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"name":"ankit","password":"supersecret123"}'

# 5. Cast a vote (voter token)
curl -X POST "http://localhost:8080/add/vote?electionId=1&electionChoiceId=1" \
  -H "Authorization: Bearer <VOTER_TOKEN>"

# 6. Check the winner
curl http://localhost:8080/winner/election/Class%20Monitor%202026 \
  -H "Authorization: Bearer <VOTER_TOKEN>"
```

---

## 🛡️ Business Rules

- A **user cannot vote twice** in the same election.
- Each **election choice must belong to an existing election**.
- A **vote must be for a choice that belongs to the election being voted in**.
- Only **admins** can create elections and candidates; only **voters** can vote.
- Usernames are **unique**; election names are **unique**.
- The **winner** is the candidate with the highest vote count in that election.
- Passwords are **never returned** in API responses and are stored **BCrypt-hashed**.

---

## ❗ Error Responses

| Status | When                                                                                   |
|--------|----------------------------------------------------------------------------------------|
| `400`  | Validation failed, or the chosen candidate does not belong to the election             |
| `401`  | Missing, invalid or expired token, or wrong login credentials                          |
| `403`  | Valid token but the role is not allowed for this endpoint                              |
| `404`  | Election, candidate or user not found, or no votes cast yet when asking for the winner |
| `409`  | Duplicate vote, or duplicate username / election name                                  |

Errors from the application return JSON:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "You have already voted in this election",
  "timestamp": "2026-09-30T05:30:00Z"
}
```

---

## ⚠️ Known Limitations

This is a learning project, not a production voting system:

- **No ballot secrecy** – admins can see which user voted for which candidate via `GET /get/votes`.
- **No election lifecycle** – elections have no open/close dates, and results are visible to any logged-in user at any time.
- **No login rate limiting** – repeated password guesses are not throttled.
- **Ties** – if candidates tie, `/winner` returns one of them arbitrarily.

---

## 👨‍💻 Author

**Ankit Gupta**
GitHub: [@Ankitsalvgupta](https://github.com/Ankitsalvgupta)