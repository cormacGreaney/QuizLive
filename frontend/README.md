# Live Quiz Frontend (React + Vite)

Dev server: http://localhost:5173

## Setup
```powershell
cd live-quiz-frontend
Copy-Item .env.example .env
# edit .env -> VITE_GOOGLE_CLIENT_ID (and VITE_API_BASE if your gateway differs)
npm install
npm run dev
```

OAuth redirect URI used: `http://localhost:5173/oauth2/callback/google/`
It must be allowed in Google Console and in the Auth service config.
