# Blood Bank and Donor Management System

## 1. Identification of Functional and Non-Functional Requirements (10 Marks)

### 1.1 Functional Requirements

1. **Donor Registration:** The system shall allow donors to register and view their next eligibility date for donation.
2. **Donation Management:** Blood bank staff shall record donations and create blood unit records.
3. **Expiry Calculation:** The system shall calculate blood unit expiry dates based on component type, such as whole blood, plasma, and platelets.
4. **Hospital Requests:** Hospitals shall submit blood requests specifying blood group, quantity, and urgency.
5. **Compatibility Matching:** The system shall identify compatible blood units and prioritize those with the earliest expiry date.
6. **Blood Unit Issuing:** Staff shall issue compatible units against approved hospital requests.
7. **Expiry Alerts:** The system shall flag blood units due to expire within three days and maintain a discard log.
8. **Donation History:** Donors and authorized staff shall be able to view donation history according to their access permissions.
9. **Stock Dashboard:** Administrators shall view available blood stock categorized by blood group and component type.
10. **User and Access Management:** The system shall provide role-based access for administrators, blood bank staff, donors, and hospitals.

### 1.2 Non-Functional Requirements

1. **Data Integrity:** An issued blood unit must not be issued again. Status and version checks shall prevent duplicate issuing.
2. **Security and Privacy:** Donor contact details shall be visible only to authorized blood bank staff.
3. **Performance:** Urgent hospital requests shall be highlighted and prioritized, and inventory searches should return results efficiently.
4. **Reliability:** The system shall preserve accurate inventory and request records during failures.
5. **Auditability:** Donation, issuing, expiry, and discard activities shall be recorded for auditing.
6. **Availability:** Authorized users should be able to access the system reliably during operational hours.
7. **Scalability:** The system should support increasing numbers of donors, hospitals, requests, and blood units.
8. **Maintainability:** Separate functional modules or microservices should make the system easier to maintain and extend.
9. **Data Consistency:** Inventory quantities and blood unit statuses must remain consistent when multiple staff members operate concurrently.
10. **Scheduled Processing:** A daily scheduled job shall identify units expiring within three days and generate the appropriate alerts.

## 2. Proposed Methodology / Architecture (10 Marks)

### 2.1 Proposed Architecture

**Approach:** Modular microservices architecture using Java and Spring Boot, with REST APIs for client and service communication.

```text
Client Applications
(Admin, Staff, Donor, Hospital)
              |
              v
        REST API Layer
(Validation, Authentication, Authorization)
              |
      +-------+------------------+
      |                          |
      v                          v
Auth & User Service        Donor Service
- User accounts            - Donor profiles
- Role-based access        - Eligibility
                           - Donations and camps
      |                          |
      +-------------+------------+
                    |
                    v
             Blood Bank Service
             - Blood inventory
             - Compatibility matching
             - Hospital requests
             - Unit issuing and discards
                    |
                    v
              Dashboard Module
              - Stock summaries
              - Expiry alerts
                    |
                    v
             Relational Database
 (Donors, Donations, Blood Units, Requests,
       Issue Records, Audit Logs)

Note: The module-to-service mapping can be refined during detailed design.
```

### 2.2 Development Methodology

1. **Requirement Analysis:** Identify user roles, functional requirements, non-functional requirements, and blood bank workflows.
2. **System Design:** Define service boundaries, database entities, REST endpoints, and access permissions.
3. **Implementation:** Develop services using Java and Spring Boot with a layered architecture.
4. **Business Logic:** Implement blood group compatibility, expiry calculation, request prioritization, and duplicate-issue prevention.
5. **Integration:** Connect services using REST APIs and Spring Cloud OpenFeign where required.
6. **Testing:** Perform unit, integration, API, validation, and concurrency tests.
7. **Deployment and Monitoring:** Deploy the application, monitor errors, and maintain audit logs.

### 2.3 Design Patterns and Java Concepts

- **Strategy Pattern:** Use `CompatibilityStrategy` to encapsulate blood group compatibility rules.
- **Factory Pattern:** Use `ComponentFactory` to create component-specific blood unit objects.
- **Builder Pattern:** Use `IssueSlip` to construct issue records.
- **Sealed Classes:** Represent the permitted blood unit status types.
- **Streams API:** Filter and sort compatible units by expiry date.
- **Spring Scheduling:** Run daily expiry checks.
- **OpenFeign:** Support communication between the blood bank and donor services.

## 3. Tools and Technologies Identified (5 Marks)

| Category             | Proposed Technology               | Purpose                                             |
|----------------------|-----------------------------------|-----------------------------------------------------|
| Programming Language | Java 21                           | Core business logic and object-oriented programming |
| Backend Framework    | Spring Boot                       | Develop REST APIs and backend services              |
| API Communication    | REST APIs, Spring Cloud OpenFeign | Client and service-to-service communication         |
| Database             | MySQL                             | Store donor, inventory, request, and audit data     |
| Security             | Spring Security, JWT              | Authentication and role-based authorization         |
| Build Tool           | Maven                             | Dependency management and project builds            |
| API Testing          | Postman                           | Test REST endpoints and responses                   |
| Development IDE      | IntelliJ IDEA                     | Coding, debugging, and testing                      |
| Version Control      | Git and GitHub                    | Source control and collaboration                    |
| Testing              | JUnit 5, Mockito                  | Unit and service-layer testing                      |
  
## Proposed Project Title

**Blood Bank and Donor Management System Using Java Spring Boot and Microservices**

> **Note:** The architecture and technologies above are proposed implementation choices. They are not all explicitly mandated by the original project specification.
