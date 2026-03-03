# 📘 RevHire P2 — Full Project Architecture & Code Deep Dive

This document provides a comprehensive explanation of every package, class, and architectural decision in the RevHire project. Use this as a reference for your mentor presentation.

---

## 🏗️ 1. Project Structural Design: N-Tier Architecture
The project follows a classic **N-Tier (Layered) Architecture**.
- **Presentation Layer:** Controllers (Web UI and REST API).
- **Service Layer:** Business logic and orchestration.
- **Data Access Layer:** Repositories (JPA).
- **Data Model:** Entities (Database) and DTOs (Data Transfer).

---

## 📦 2. Package-by-Package Explanation

### 💾 `com.rev.app.entity`
**Purpose:** Maps Java classes to Database tables using JPA/Hibernate.
- **Key Classes:** `User`, `Job`, `JobSeeker`, `Employer`, `Company`, `Application`, `Resume`, `Notification`.
- **Why we use it:** To represent the core business data. Hibernate handles all the complex SQL behind the scenes using these classes.
- **Mentor Tip:** Mention **Relationships**. e.g., "A `Job` has a `@ManyToOne` relationship with `Employer`, ensuring data integrity."

### 📝 `com.rev.app.dto`
**Purpose:** "Data Transfer Objects" — simplified versions of entities used for moving data between the client and server.
- **Why we use it:** 
    1. **Security:** We don't want to expose internal entity fields like `User.password` to the network.
    2. **Performance:** DTOs only contain the data the frontend actually needs.
    3. **Decoupling:** If the database schema changes, the DTO (Contract) remains stable.

### 🔄 `com.rev.app.mapper`
**Purpose:** Converts Entities to DTOs and vice versa.
- **Key Classes:** `JobMapper`, `ApplicationMapper`, `JobSeekerMapper`, etc.
- **Why we use it:** To keep the conversion logic out of the Services and Controllers. This follows the **Single Responsibility Principle**.
- **Mentor Tip:** Mention the **Refactoring**. "I split the monolithic `AppMapper` into 6 domain-specific mappers to improve modularity and testability."

### 🏛️ `com.rev.app.repository`
**Purpose:** Interfaces that extend `JpaRepository`.
- **Why we use it:** Spring Data JPA automatically provides CRUD methods (`save`, `findById`, `delete`).
- **Custom Logic:** We used **JPQL (Java Persistence Query Language)** for advanced tasks like `searchJobsAdvanced()`.

### 🧠 `com.rev.app.service`
**Purpose:** The "Brain" of the application. Handles all calculations, validation, and database orchestration.
- **Key Classes:** `JobServiceImpl`, `ApplicationServiceImpl`, `UserServiceImpl`.
- **Why we use it:** It separates business rules from the user interface.
- **Mentor Tip:** Explain **Transactional Integrity**. "In `applyToJob`, we check if a seeker has a resume, if they've already applied, and then save the application — all in one business unit."

### 🌐 `com.rev.app.controller` & `com.rev.app.rest`
**Purpose:** Entry points for user requests.
- **`controller` (Web):** Returns HTML pages using Thymeleaf.
- **`rest` (API):** Returns JSON data.
- **Why we use both:** The project is a **Hybrid Application**. It serves a traditional web portal while providing a modern REST API secured by **JWT** for mobile or external integration.

### 🛡️ `com.rev.app.config`
**Purpose:** Infrastructure setup (Security, JWT, Beans).
- **Key Classes:** `SecurityConfig`, `JwtUtil`, `JwtAuthenticationFilter`.
- **Why we use it:** 
    1. **Spring Security:** Protects routes.
    2. **BCrypt:** Hashes passwords (no plain text in DB).
    3. **JWT:** Enables stateless authentication for the REST APIs.

### ⚠️ `com.rev.app.exception`
**Purpose:** Centralized error handling.
- **Why we use it:** To return user-friendly error messages (e.g., `Job Not Found`) instead of generic server errors (500).

---

## ⚙️ 3. Workflow walkthrough: "Applying for a Job"
If a mentor asks: *"What happens behind the code when a seeker clicks Apply?"*

1. **REST Entry:** `ApplicationRestController` receives a POST request with `ApplicationDTO`.
2. **Service Call:** It passes the DTO to `applicationService.applyToJob(dto)`.
3. **Validation:** The service checks:
   - Does the seeker exist? (`jobSeekerRepository`)
   - Does the job exist? (`jobRepository`)
   - Already applied? (`existsByJobSeekerAndJob`)
4. **Mapping:** The service uses `JobSeekerMapper` or internal logic to build a new `Application` entity.
5. **Persistence:** The entity is saved via `applicationRepository.save()`.
6. **Notification:** The `NotificationService` is called to alert the employer.
7. **Response:** The service converts the saved Entity back to a Result-DTO and the Controller returns it as JSON (201 Created).

---

## 🎖️ 4. Advanced Engineering Decisions (High-Score Answers)

### ❓ Why JWT instead of just Sessions?
> "Sessions are stateful and stored on the server's memory. **JWT (JSON Web Tokens)** are stateless and stored on the client. This makes our REST API scalable across multiple servers and compatible with mobile apps without needing a cookie-based session."

### ❓ Why use custom Mappers instead of MapStruct?
> "By writing our own mappers (e.g., `JobMapper`), we avoided adding another heavy dependency and maintained full control over complex mappings, like calculating `applicantCount` or formatting nested company details manually."

### ❓ How did you handle Password Security?
> "I implemented a `PasswordEncoder` bean using **BCrypt**. During registration, the plain password is never saved; only the cryptographic hash is stored. This follows industry standards for data protection."

---

## 🧪 5. Testing Summary (QA)
- **93 Tests:** Across 16 classes.
- **Unit (Service):** Verified business rules without database dependencies using **Mockito mocks**.
- **Integration (REST):** Verified API contracts and error handling using **MockMvc**.

---
**Prepared for Mentor Presentation — March 03, 2026**
