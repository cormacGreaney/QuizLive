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
        nav('/admin/quizzes', { replace: true });
      } catch (e: any) {
        console.error('Auth exchange error:', e);
        setErr(e?.message || 'Login failed');
        nav('/login', { replace: true });
      }
    })();
  }, [loc.search, nav, setTokens, setProfile]);

  if (err) return <div style={{ padding: 24, color: 'crimson' }}>Login error: {err}</div>;
  return <div style={{ padding: 24 }}>Finishing sign-in…</div>;
}
