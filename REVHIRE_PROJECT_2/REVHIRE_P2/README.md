# RevHire - Full-Stack Job Portal 

RevHire is a robust, monolithic Spring Boot application designed to bridge the gap between talented job seekers and industry-leading employers. Built with a focus on visual excellence and technical integrity, it offers a dual-architecture approach supporting both **Thymeleaf MVC** and **RESTful APIs**.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 8, Spring Boot 2.7.x, Spring Security, Spring Data JPA |
| **Frontend** | Thymeleaf, Vanilla CSS, JavaScript (Responsive Design) |
| **Persistence** | Oracle SQL Database (ojdbc8) |
| **Architecture** | Hybrid (MVC + REST API), Service-Repository Pattern |
| **Tools** | Maven, Log4j2, JUnit 4, Mockito |

---

## ✨ Core Functional Requirements

### 🧑‍💼 For Job Seekers
- **Profile Management**: Build a comprehensive personal profile with location and employment history.
- **Resume System**: 
    - **Textual Builder**: Create structured sections (Objective, Education, Experience, Skills, Certifications).
    - **File Upload**: Securely upload PDF/DOCX resumes (up to 2MB).
- **Advanced Job Search**: Powerful filtering by keywords, location, experience, salary, and **posted date**.
- **Application Tracking**: 
    - One-click application using saved resumes.
    - View status tracking (Applied/Shortlisted/Rejected).
    - Withdraw applications with optional feedback.
- **Engagement**: Bookmark "Favorite" jobs and receive real-time **in-app notifications**.

### 🏢 For Employers
- **Talent Management**:
    - Post comprehensive job listings with detailed requirements.
    - **Mark as Filled**: Manage job lifecycle (Close/Reopen/Mark as Filled).
    - View **Dashboard Statistics** (Total Apps, Pending Reviews, Active Jobs).
- **Applicant Processing**:
    - Advanced filtering of candidates by experience and skills.
    - **Bulk Actions**: Shortlist or reject multiple applicants at once.
    - **Internal Tracking**: Add private notes to candidate applications.
- **Company Branding**: Register and manage detailed corporate profiles.

---

## 🏗️ Technical Highlights

- **The "Both" Approach**: Seamlessly switch between server-side Thymeleaf rendering and JSON-based REST endpoints.
- **Data Integrity**: Enforced via Spring's transactional management across all services.
- **Security**: 
    - Role-Based Access Control (RBAC).
    - BCrypt password encryption.
    - Session-based authentication for UI and secured API endpoints.
- **Architecture**: Strict adherence to a 3-layer monolithic pattern for clean separation of concerns.

---

##  Getting Started

### 1. Database Configuration
Update `src/main/resources/application.properties` with your Oracle DB details:
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=system
spring.datasource.password=root
```

### 2. Build & Run
```bash
# Clean and compile
mvn clean install

# Launch the application
mvn spring-boot:run
```
The application will be available at `http://localhost:8080`.

---

## 📝 Documentation & Artifacts
- **ER Diagram**: Detailed schema design available in `DOCS.md`.
- **Architecture**: Component interaction flow documented in `DOCS.md`.
- **Verification**: Core features verified via `mvn test` (JUnit 4/Mockito).
- **Checklist**: See `IMPLEMENTATION_TESTING_CHECKLIST.md` for E2E verification status.

---

## ✅ Definition of Done
- [x] Functional registration/login for Seeker & Employer roles.
- [x] Full Job Posting and Applicant Management lifecycle.
- [x] Advanced Search and Filtering logic.
- [x] Textual and Document-based Resume support.
- [x] In-app Notification system.
- [x] Responsive layout for all views.
- [x] Comprehensive Exception Handling.
- [x] Documented ERD and Architecture.
