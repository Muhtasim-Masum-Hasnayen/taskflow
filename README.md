# TaskFlow
Team & project management platform (React + Vite + TypeScript, Spring Boot 3.5 on Java 25, PostgreSQL).

## Status
**Step 1a of 5.** Implemented: full database schema (Flyway V1), JWT auth with rotating refresh tokens, `/users/me`, React login/register/protected routes.
Not yet implemented: email verification/reset, organizations & RBAC, teams, projects, tasks/Kanban, comments, attachments, notifications, dashboards, reports, landing page. See `docs/architecture.md`.

## Verification status
| Part | Status |
|---|---|
| Frontend: `npm run build`, `npm test` | Ran successfully when generated (4 tests passing) |
| Backend: Java 25 `mvn verify` | Passed (7 tests) |
| Docker, CI workflows, deployment | Not run |

## Run locally
1. `docker compose up db` (or any PostgreSQL 14+ with a `taskflow` database).
2. Backend (JDK 25, Maven 3.6.3+): `cd backend && cp .env.example .env`, export the variables, `mvn spring-boot:run`. Swagger UI: http://localhost:8080/swagger-ui.html
3. Frontend: `cd frontend && cp .env.example .env && npm install && npm run dev` (http://localhost:5173).
4. Tests: `mvn test` (backend, H2 profile `test`), `npm test` (frontend).

## Docs
`docs/architecture.md`, `docs/deployment.md`. License: MIT (add a LICENSE file).
