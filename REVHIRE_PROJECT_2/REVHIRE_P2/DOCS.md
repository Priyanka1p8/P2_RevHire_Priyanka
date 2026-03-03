# RevHire Project Documentation

## 1. Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    USER ||--o| JOB_SEEKER : has
    USER ||--o| EMPLOYER : has
    EMPLOYER ||--o| COMPANY : belongs_to
    EMPLOYER ||--o| JOB : posts
    COMPANY ||--o| JOB : owns
    JOB ||--o{ APPLICATION : receives
    JOB_SEEKER ||--o{ APPLICATION : submits
    JOB_SEEKER ||--o| RESUME : has
    RESUME ||--o{ APPLICATION : used_in
    APPLICATION ||--o{ APPLICATION_NOTE : has
    USER ||--o{ NOTIFICATION : receives
    JOB_SEEKER ||--o{ SAVED_JOB : bookmarks
    JOB ||--o{ SAVED_JOB : bookmarked_as
```

### Table Descriptions
- **Users**: Core credentials and RBAC (ROLE_SEEKER, ROLE_EMPLOYER).
- **JobSeekers**: Personal profiles for candidates.
- **Employers**: Profile for hiring managers.
- **Companies**: Corporate details linked to employers.
- **Jobs**: Job listings with requirements and metadata.
- **Resumes**: Textual data and file paths for candidate profiles.
- **Applications**: Join table for Jobs/Seekers with status and cover letters.
- **ApplicationNotes**: Internal feedback for specific applications.
- **Notifications**: System alerts for users.
- **SavedJobs**: Bookmarks for job seekers.

---

## 2. Application Architecture

```mermaid
graph TD
    Client[Web Browser] --> Controller[Spring MVC Controllers]
    Client --> REST[REST API Controllers]
    Controller --> Service[Service Layer]
    REST --> Service
    Service --> Repository[Repository Layer / Spring Data JPA]
    Repository --> DB[(Oracle SQL Database)]
    Service --> Security[Spring Security]
    Service --> Logging[Log4j2]
```

### Layer Responsibilities
1.  **View Layer (Thymeleaf/CSS/JS)**: Responsive UI for all user roles.
2.  **Controller Layer**: Handles HTTP requests, manages session/security, and routes to appropriate views.
3.  **Service Layer**: Contains business logic, validation, and transaction management.
4.  **Data Access Layer**: JPA repositories for boilerplate CRUD and custom JPQL queries.
5.  **Security Layer**: Role-based access control and password encryption.

---

## 3. Core Workflow Sequence

```mermaid
sequenceDiagram
    participant JS as Job Seeker
    participant APP as RevHire System
    participant EMP as Employer
    
    JS->>APP: Search & Apply for Job
    APP->>APP: Validate Resume & Duplicate Check
    APP-->>JS: Success Notification
    EMP->>APP: View Job Applicants
    APP-->>EMP: Display List & Resume
    EMP->>APP: Update Status to SHORTLISTED
    APP->>APP: Log Internal Note
    APP-->>JS: Push Notification: "You've been shortlisted!"
    JS->>APP: Track Application Status
```
