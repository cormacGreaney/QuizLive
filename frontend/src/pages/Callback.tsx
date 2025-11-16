import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { API_BASE, postJson } from '../lib/api';
import { useAuth } from '../auth/AuthProvider';

type LoginResp = {
  accessToken: string;
  refreshToken: string;
  profile: {
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
    const params = new URLSearchParams(loc.search);
    const code = params.get('code');
    const redirectUri = `${window.location.origin}/oauth2/callback/google/`;

    if (!code) {
      setErr('Missing ?code from Google.');
      return;
    }

    (async () => {
      try {
        const resp = await postJson<LoginResp>(`${API_BASE}/auth/login/google`, { code, redirectUri });
        setTokens(resp.accessToken, resp.refreshToken);
        setProfile(resp.profile);
        nav('/dashboard', { replace: true });
      } catch (e: any) {
        setErr(e.message || 'Login failed');
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