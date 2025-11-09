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

  if (err) return <div style={{ padding: 24, color: 'crimson' }}>Login error: {err}</div>;
  return <div style={{ padding: 24 }}>Finishing sign-in…</div>;
}
