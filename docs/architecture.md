# Architecture & plan
Modular monolith (Spring Boot 3.5, Java 25) + React/Vite/TypeScript SPA. Tenancy: shared schema, `org_id` on tenant tables, enforced in the service layer.
Auth: 15-min JWT access token (kept in memory) + rotating refresh token (httpOnly cookie, SHA-256 hashed in `refresh_token`, family revocation on reuse).
Roles live on `organization_member.role`. Full schema: `backend/src/main/resources/db/migration/V1__init.sql`.

## Roadmap
| Step | Scope | Status |
|---|---|---|
| 1a | Scaffold, full V1 schema, register/login/refresh/logout, `/users/me`, frontend auth shell | **Included** |
| 1b | Email verify/reset, orgs, invitations, RBAC AccessPolicy, isolation tests, rate limiting | Not started |
| 2 | Teams, projects, tasks, Kanban (dnd-kit) | Not started |
| 3 | Comments, mentions, attachments (R2/S3), notifications, activity log | Not started |
| 4 | Dashboards, calendar, reports (CSV/PDF) | Not started |
| 5 | Landing page, dark mode, shadcn/ui, security review, deployment hardening | Not started |
