# QuizLive – Monorepo

Services:
- api-gateway/ – handles api traffic
- auth-service/ – Google OAuth + JWT
- qms-service/ – quiz/question management
- rts-service/ – realtime websocket
- frontend-client/ – react client

## Dev quick start
1. Clone repo
2. Open **Docker Desktop** (keep it running).
3. PowerShell:
   cd to .\infra
   Copy-Item .env.example .env
   Edit .env and fill JWT_SECRET + Google creds
   docker compose up --build
4. If front end errors on start up cd to frontend folder in powershell and run npm install to regen you package-lock.json
