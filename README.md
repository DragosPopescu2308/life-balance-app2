# Life Balance App

Life Balance App is a full-stack web application designed to help users manage their finances and personal goals in one place.

The application allows users to track incomes, expenses, savings, and financial goals through a modern web interface.

---

## Features

- User registration and login
- Dashboard with financial overview
- Track incomes and expenses
- Manage savings and financial goals
- Profile management
- Attachments for receipts and income documents

---

## Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security (Session-based authentication)
- Spring Data JPA
- MySQL

### Frontend
- React
- Vite
- TailwindCSS

---

## Security

Authentication and authorization are implemented using **Spring Security with session-based authentication**.

The backend manages user sessions and protects endpoints through the security configuration.  
JWT tokens are **not used in this implementation**, as the application relies on traditional server-side sessions.

---

## Architecture

The backend follows a layered architecture:

Controller → Service → Repository → Database


Main components include:

- Controllers for REST endpoints
- Services for business logic
- Repositories for database access
- DTOs for request and response models
- Global exception handling

---

## Future Improvements

- Docker containerization
- Deployment to a cloud platform
- Improved analytics and financial charts
- Advanced budgeting features

---

## Author

Dragos Popescu  
Aspiring Software Developer
