import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { API_BASE, postJson } from '../lib/api';
import { useAuth } from '../auth/AuthProvider';

type LoginRespLoose = {
  accessToken?: string;
  access_token?: string;
  token?: string;
  refreshToken?: string;
  refresh_token?: string;
  profile?: {
    id: number;
    email: string;
    name: string;
    pictureUrl?: string;
    role?: string;
    provider?: string;
  };
};

export default function Callback() {
  const nav = useNavigate();
  const loc = useLocation();
  const { setTokens, setProfile } = useAuth();
  const [err, setErr] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      const params = new URLSearchParams(loc.search);
      const code = params.get('code');
      if (!code) {
        setErr('Missing code');
        nav('/login', { replace: true });
        return;
      }

      // MUST match the value used in Login.tsx (and whitelisted in Google & auth-service)
      const redirectUri = `${window.location.origin}/oauth2/callback/google`;

      try {
        const resp = await postJson<LoginRespLoose>(`${API_BASE}/auth/login/google`, { code, redirectUri });

        // Be tolerant to different field names
        const access =
          resp.accessToken ?? resp.access_token ?? resp.token ?? null;
        const refresh =
          resp.refreshToken ?? resp.refresh_token ?? '';

        if (!access) {
          console.error('Auth exchange payload had no access token:', resp);
          setErr('Login failed (no access token)');
          nav('/login', { replace: true });
          return;
        }

        setTokens(access, refresh);
        setProfile(resp.profile ?? null);

        // Go to the manager (or wherever)
        nav('/dashboard', { replace: true });
      } catch (e: any) {
        console.error('Auth exchange error:', e);
        setErr(e?.message || 'Login failed');
        nav('/login', { replace: true });
      }
    })();
  }, [loc.search, nav, setTokens, setProfile]);

  return (
    <div style={{
      maxWidth: 480,
      margin: '80px auto',
      padding: '2rem',
      borderRadius: '16px',
      background: 'linear-gradient(to right, #fdfbfb, #ebedee)',
      boxShadow: '0 8px 24px rgba(0,0,0,0.1)',
      fontFamily: 'Segoe UI, sans-serif',
      textAlign: 'center',
      color: '#333'
    }}>
      {err ? (
        <>
          <h2 style={{
            fontSize: '1.75rem',
            marginBottom: '1rem',
            color: '#e74c3c'
          }}>
            Login Error
          </h2>
          <p style={{
            fontSize: '1rem',
            color: '#c0392b',
            backgroundColor: '#ffe6e6',
            padding: '1rem',
            borderRadius: '8px',
            boxShadow: '0 2px 6px rgba(0,0,0,0.05)'
          }}>
            {err}
          </p>
        </>
      ) : (
        <>
          <h2 style={{
            fontSize: '1.75rem',
            marginBottom: '1rem',
            background: 'linear-gradient(to right, #00c6ff, #0072ff)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent'
          }}>
            Finishing Sign-In…
          </h2>
          <p style={{
            fontSize: '1rem',
            color: '#555'
          }}>
            Please wait ... We're verifying your account and getting things ready.
          </p>
        </>
      )}
    </div>
  );
}