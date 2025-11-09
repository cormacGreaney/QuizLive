# Live Quiz – Monorepo (Windows-friendly)

Services:
- api-gateway/ – routes /auth/**, /qms/**, /ws/**
- auth-service/ – Google OAuth + JWT (/auth/*)
- qms-service/ – quiz/question management (/qms/*)
- 
ts-service/ – realtime websocket (/ws)

## Dev quick start (later)
1. Open **Docker Desktop** (keep it running).
2. PowerShell:
   `powershell
   cd .\infra
   Copy-Item .env.example .env
   # Edit .env and fill JWT_SECRET + Google creds
   docker compose up --build
