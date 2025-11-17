Auth Service – What it is, how it works, and how to use it
==========================================================
-Cormac Greaney

Plain-English overview
----------------------
This service handles user sign-in for our app. It lets people log in with Google,
creates/updates a user record in MySQL, and gives the frontend a pair of tokens:
- an **access token** (short-lived) to call protected APIs
- a **refresh token** (long-lived) to get a new access token without logging in again

It’s a small Spring Boot app that runs on http://localhost:8081 in dev.

How Google sign-in works (high level)
-------------------------------------
1) Frontend sends the user to Google’s consent page.
2) Google redirects back to our frontend with a one-time `code`.
3) Frontend posts `{ code, redirectUri }` to **POST /auth/login/google**.
4) The auth service exchanges that code with Google for a user profile.
5) We upsert the user in our `users` table (MySQL).
6) We mint a JWT **access token** + a **refresh token** and return them to the frontend.
7) The frontend stores tokens (e.g., memory/localStorage) and uses the access token
   in the `Authorization: Bearer <token>` header when calling protected APIs.

What lives in the JWT
---------------------
- `sub`: internal user id
- `iss`: "quiz-auth"
- `exp`: expiry
- claims: `uid`, `email`, `name`, `role`, `provider`

Stack & ports
-------------
- Java 17+ / Spring Boot 3.3
- MySQL 8 (DB name: `quiz_auth`)
- Service port: `8081`

Config (env vars)
-----------------
Set these before running:
- `DB_USERNAME` – MySQL user (dev: `root`)
- `DB_PASSWORD` – MySQL password
- `JWT_SECRET`  – long random secret (>= 32 chars, base64 good)
- `GOOGLE_CLIENT_ID` – from Google Cloud console
- `GOOGLE_CLIENT_SECRET` – from Google Cloud console

In `application.yml`, we allow a development redirect URI list, e.g.:
```
google.oauth.allowedRedirectUris:
  - http://localhost:5173/oauth2/callback/google
  - http://localhost:5173/oauth2/callback/google/
```

Endpoints you’ll actually use
-----------------------------
1) **POST /auth/login/google**
   - Body: `{ "code": "<google_code>", "redirectUri": "<exact callback URL>" }`
   - Returns: `{ accessToken, refreshToken, profile }`
   - Notes: `redirectUri` must exactly match what Google sent you back to.

2) **GET /auth/me**
   - Header: `Authorization: Bearer <accessToken>`
   - Returns the current user profile (id, email, name, role, provider).

3) **POST /auth/verify**
   - Body: `{ "token": "<any JWT>" }`
   - Returns whether the token is valid and when it expires.

4) **POST /auth/refresh**
   - Body: `{ "refreshToken": "<refresh token>" }`
   - Returns new `{ accessToken, refreshToken }`

How to run in development
-------------------------
Option A – Local MySQL installed:
1) Create DB `quiz_auth` (or just let Hibernate create tables automatically).
2) Export env vars for DB and Google.
3) Run `mvn spring-boot:run`.
4) Health is at `GET /actuator/health` → `{"status":"UP"}`.

Option B – Docker MySQL:
1) `docker compose up -d mysql` (from the project root).
2) Export env vars and run `mvn spring-boot:run`.

Quick manual test flow (no frontend required)
---------------------------------------------
1) Serve `oauth-test.html` at `http://localhost:5173/oauth2/callback/google/`.
2) Add that **exact** redirect URI in Google Cloud console AND in `application.yml`.
3) Open the page, click “Sign in with Google”, consent.
4) You should see `{ accessToken, refreshToken, profile }` on success.
5) Call `/auth/me` with the access token to prove protected endpoint works.

Database model (short)
----------------------
Table `users`:
- `id` (PK, auto) – internal ID
- `provider` (enum) – e.g., GOOGLE
- `providerId` – user id from the provider
- `email`, `name`, `pictureUrl`
- `role` (enum) – USER/ADMIN
- created/updated timestamps

Security model (short)
----------------------
- Stateless JWT (HS256) via `Authorization: Bearer <access>`
- CORS is open in dev; lock it down per environment later
- Sessions disabled; no cookies


What other services need to know
--------------------------------------
- For protected endpoints behind the gateway, they must pass the JWT through.
- Other microservices can either validate JWT locally (same `JWT_SECRET` and issuer) or call `/auth/verify`.
- The gateway should allow `/auth/**` to reach this service without JWT.

Production notes (later)
------------------------
- Replace `ddl-auto: update` with proper Flyway migrations.
- Restrict CORS to known frontends.
- Don’t run as DB root; create a least-privilege DB user.
- Rotate secrets safely; align token lifetimes with product needs.
- Add structured logging and metrics if needed.