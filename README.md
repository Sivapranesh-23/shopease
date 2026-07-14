# 🛍️ ShopEase — Full-Stack Containerized E-Commerce Platform

[![ShopEase CI](https://github.com/Sivapranesh-23/shopease/actions/workflows/ci.yml/badge.svg)](https://github.com/Sivapranesh-23/shopease/actions)
[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat&logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![React Version](https://img.shields.io/badge/React-19-blue.svg?style=flat&logo=react)](https://react.dev/)
[![Docker Support](https://img.shields.io/badge/Docker-Enabled-blue.svg?style=flat&logo=docker)](https://www.docker.com/)

ShopEase is a modern, high-performance, containerized full-stack e-commerce application. It features a responsive React storefront, a secure token-based Spring Boot 3 REST API, automated multi-container developer pipelines, database search fallbacks, and real-time shopping cart and wishlist synchronization.

---

## 🌟 Features

### 🛍️ Customer Experience
* **Persistent Shopping Cart & Wishlist**: Guest carts and wishlists merge automatically to the user's database profile upon login. Caching via `localStorage` keeps navbar counters updated in real time.
* **Secure Stripe Payment Integration**: Mounts Stripe Elements dynamically for checkout validation, falling back to a stubbed mock card form if no Stripe key is configured.
* **Auto-Reserved Inventory**: Stock is reserved immediately upon placement of a pending order. Inventory is captured on successful payment, or released back to the store if the order is cancelled.
* **HTML Email Notifications**: Sends branded, rich HTML templates asynchronously for order confirmation, shipping alerts, and password resets using a fail-safe Spring Mail configurations.

### 🛡️ Admin Management
* **Catalog Management**: Creation, deletion, and updating of products and parent/child category hierarchies.
* **Bulk Inventory Operations**: Restock catalog items in bulk.
* **Analytics Dashboard**: Live calculation of total revenue, orders, active user profiles, low-stock warnings, and historical monthly sales reports.

---

## 🛠️ Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Frontend** | React 19, Vite, Javascript, Vanilla CSS | Single Page App (SPA) Client |
| **Backend** | Java 21, Spring Boot 3.3, Spring Security | REST API & Security Filter Layer |
| **Authentication**| JWT (JSON Web Tokens) | Stateless, role-based authorization |
| **Database** | MySQL 8.4, H2 (test runtime), Flyway | Schema migrations & Seed data |
| **Cache & Limit** | Redis 7.4 | Cart buffers and rate-limiting |
| **Search Engine** | Elasticsearch 8.15 | Fast index searches (with SQL query fallback) |
| **Sandbox SMTP** | Mailpit | Visual review of dispatched HTML emails |

---

## ⚙️ Architectural Highlights

### ⚡ Elastic Search SQL Failover
If Elasticsearch is down or unconfigured, the application catches the connection exception and automatically redirects catalog queries to the relational database using JPA Criteria queries, keeping the search bar operational under any infrastructure status:
```java
// Fallback path in SearchServiceImpl.java when Elasticsearch is offline
private Page<ProductResponse> fallbackSearch(String query, Pageable pageable) {
    String searchTerm = "%" + query.trim().toLowerCase() + "%";
    return productRepository.findAll((root, ignoredQuery, cb) -> cb.and(
            cb.isTrue(root.get("active")),
            cb.or(
                    cb.like(cb.lower(root.get("name")), searchTerm),
                    cb.like(cb.lower(root.get("summary")), searchTerm),
                    cb.like(cb.lower(root.get("description")), searchTerm)
            )
    ), pageable).map(productMapper::toResponse);
}
```

---

## 🚀 Quick Start Guide

### Prerequisites
Make sure you have the following installed on your machine:
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (must be running)
* [Java 21 JDK](https://adoptium.net/temurin/releases/?version=21)
* [Node.js 20+](https://nodejs.org/)
* [Maven 3+](https://maven.apache.org/)

---

### Step 1: Start Infrastructure Containers
Start the MySQL database, Redis cache, Elasticsearch engine, and Mailpit sandbox server in the background:
```bash
docker-compose up -d mysql redis elasticsearch mailpit
```
*Wait 10 seconds for the databases to fully boot and seed.*

### Step 2: Start the Spring Boot Backend API
Navigate to the backend directory and launch the application:
```bash
cd backend
mvn spring-boot:run
```
*The API is now running at **`http://localhost:8080`**.*
*Swagger UI docs are available at **`http://localhost:8080/swagger-ui.html`**.*

### Step 3: Start the React Storefront
Open a new terminal window, navigate to the frontend directory, install packages, and boot up the Vite dev server:
```bash
cd frontend
npm install --legacy-peer-deps
npm run dev
```
*The storefront client will launch at **`http://localhost:5173`**.*

---

## 🧪 Testing

The backend includes a comprehensive unit and integration test suite (incorporating mock environments for Redis and Elasticsearch, running on an in-memory H2 database runtime):

To run all tests:
```bash
cd backend
mvn clean test
```

Test coverage includes:
* **Context Startup Validation**: `ShopeaseApplicationTests.java` checks for auto-wiring / duplicate bean failures.
* **Checkout Lifecycle Integration**: `CheckoutIntegrationTest.java` verifies the complete placement-to-payment lifecycle.
* **Unit Tests**: Mocks service interactions with Mockito.

---

## 🐳 Docker Production Build
To test the production container build locally:
```bash
docker-compose up --build
```
This compiles both apps via multi-stage Docker builds. The React storefront compiles its assets and loads them inside an **Nginx** container configured to handle client-side SPA routing fallbacks.

---

## ☁️ 100% Free Production Deployment

You can host this application completely free of charge by utilising the serverless free tiers of the following platforms:

1. **Frontend**: Host on **Vercel** or **Netlify** (Free Static Site CDNs).
2. **Backend**: Host on **Render** or **Koyeb** (Free serverless Java web service instances).
3. **Database (MySQL)**: Host on **TiDB Cloud Serverless** (5GB free serverless MySQL compatible cloud DB).
4. **Session Cache**: Host on **Upstash Redis** (10,000 requests/day free serverless Redis).
5. **Search Failover**: Let the backend query the database search fallback (requires zero Elasticsearch cloud costs).
