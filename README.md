# TROSIVI (In-progress)

## 1 Project Description
TROSIVI is a modular room rental management system designed to simplify student housing operations. The system aims to provide an end-to-end solution for managing tenants, rooms, contracts, utility services, and monthly billing. By centralizing all rental management tasks, TROSIVI helps reduce administrative overhead, avoid errors in billing, and provide insightful analytics for property managers.

Currently, the system is **in development**, with the **Customer Management module fully implemented**. Other modules, including room booking, contract handling, service registration, and billing, are planned and under development.

## 2 Tech Stack

### 2.1 Backend
- **Java 21**
- **Spring Boot 3**
- **Spring Cloud**: Enables microservice architecture using:
    - **API Gateway**: Centralizes requests to various modules
    - **Eureka**: Service discovery and registration

### 2.2 Frontend
- **ReactJS** and **Material UI**

### 2.3 Database & Caching**
- **PostgreSQL**
- **Redis**

### 2.4 Deployment & Architecture
- Microservices architecture to isolate functional modules
- Each module communicates through REST APIs via the API Gateway
- Designed to scale horizontally by adding service instances

## 3 Functional Modules

### 3.1 Customer Management (Completed)
- Add, edit, and delete tenant profiles
- Store tenant personal information, contact details, and rental history
- Search and filter tenants for administrative tasks
- Prepare data for bookings, contracts, and billing

### 3.2 Planned Modules

#### 3.2.1 Check-in & Room Assignment
- Handle tenant check-in, whether pre-booked or walk-in
- Workflow:
    1. Assign tenant to an available room
    2. Create a rental contract linked to the tenant and room
    3. Register additional services (electricity, water, internet, etc.)
- Track room occupancy status in real time

#### 3.2.2 Monthly Billing & Payment
- Calculate monthly charges including:
    - Room rent
    - Registered services
    - Utility usage (electricity, water)
- Record tenant payments and manage outstanding balances
- Generate monthly invoices for each tenant

#### 3.2.3 Revenue-based Customer Analytics
- Analyze tenant data based on revenue contributions
- Generate reports showing:
    - Top-paying tenants
    - Revenue by room or service
    - Monthly and cumulative revenue trends
- Support property management decisions with data-driven insights

## 4 Database Design
- Relational schema includes:
    - **Tenants**: Personal info, rental history
    - **Rooms**: Availability, type, rates
    - **Contracts**: Terms, associated tenants, start/end dates
    - **Services**: Utility subscriptions and usage
    - **Payments**: Billing, status, history

## 5 Future Improvements
- Complete remaining modules (booking, contracts, services, billing)
- Enhance user interface and experience
- Implement role-based access control
- Improve system scalability and performance