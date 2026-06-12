# QuizLive – Monorepo
Screenshots:
Quiz Admin:
<img width="1896" height="912" alt="Screenshot 2026-06-12 162113" src="https://github.com/user-attachments/assets/cda4438b-396b-4e46-b2d1-94610748abb9" />

Quiz Participant:
<img width="1880" height="880" alt="Screenshot 2026-06-12 162027" src="https://github.com/user-attachments/assets/42c42da3-6c52-4967-8c12-866cf983315c" />

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
   cd to .\infra,
   Copy-Item .env.example .env,
   Edit .env and fill JWT_SECRET + Google creds,
   docker compose up --build
4. If front end errors on start up cd to frontend folder in powershell and run npm install to regen you package-lock.json
