# Deployment (documented, not yet tested)
- **Database:** managed PostgreSQL (Neon/Supabase/Render). Flyway runs on backend startup.
- **Backend:** Render Docker web service from `backend/`. Env: `DB_URL` (JDBC form), `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ORIGINS=https://<your-vercel-domain>`, `COOKIE_SECURE=true`, `COOKIE_SAMESITE=None`. Health check: `/actuator/health`.
- **Frontend:** Vercel, root `frontend/`, env `VITE_API_URL=https://<backend-host>/api/v1`. `vercel.json` provides SPA rewrites.
- Cross-site cookies (Vercel -> Render) require `SameSite=None; Secure`; a shared custom parent domain is more robust.
