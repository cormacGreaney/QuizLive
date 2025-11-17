# Dev Contract (Local)

## Ports
- Frontend: http://localhost:5173
- Gateway:  http://localhost:8080
- Auth:     http://localhost:8081
- QMS:      http://localhost:8082  (placeholder)
- RTS (WS): ws://localhost:8085    (placeholder)

## Routes via Gateway
- `/auth/**` → auth-service (8081)
- `/qms/**`  → qms-service (8082)
- `/ws/**`   → rts-service (8085) — WebSocket; preserve `?access_token=...`

## CORS (dev)
- Allowed origin: `http://localhost:5173`

## OAuth Redirect URIs (dev)
- `http://localhost:5173/oauth2/callback/google/`
- (Optionally also allow without trailing slash)

## Env (quick reference)
- **frontend**: `VITE_API_BASE`, `VITE_GOOGLE_CLIENT_ID`
- **auth**: `JWT_SECRET`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GOOGLE_ALLOWED_REDIRECT_URIS`, DB creds/URL
- **gateway**: none special (dev), just CORS + routes
