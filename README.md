# ShopEase

ShopEase is a full-stack e-commerce application with a responsive React storefront, a role-protected admin console, and a Spring Boot API. It includes catalog search, guest cart and wishlist persistence, JWT authentication, checkout, stubbed payments, order history, inventory controls, and reporting.

## Stack

- React 19, React Router 7, Vite 8
- Spring Boot 3.3, Java 21, Spring Security/JWT, JPA, Flyway
- MySQL 8, Redis, Elasticsearch, and Mailpit for local infrastructure

## Run locally

Requirements: Node.js 20+, Java 21, Maven 3.9+, and Docker Desktop for the backing services.

1. Copy `.env.example` to `.env` if you want to override the defaults.
2. Start infrastructure:

   ```bash
   docker compose up -d
   ```

3. Start the API:

   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. In a second terminal, start the frontend:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

Open `http://localhost:5173`. API documentation is at `http://localhost:8080/swagger-ui.html`, and captured development email is at `http://localhost:8025`.

The storefront falls back to its bundled catalog and local cart/wishlist if the API is offline. Authentication, checkout, orders, and admin mutations intentionally require the backend.

## Demo accounts

Seeded accounts all use `password123`:

- Admin: `admin@shopease.com`
- Customer: `julian@example.com`

## Checks

```bash
npm run check
cd backend && mvn test
```

The payment integration is a development stub: it creates and confirms a local payment intent but never contacts or charges a real payment provider.

## Main API areas

- `/api/auth`, `/api/products`, `/api/categories`
- `/api/cart`, `/api/orders`, `/api/payments`
- `/api/admin/products`, `/api/admin/categories`, `/api/admin/orders`, `/api/admin/users`, `/api/admin/reports`

Production deployments should provide a strong `APP_JWT_SECRET`, real database credentials, restricted CORS origins, and a real payment provider/webhook implementation.
