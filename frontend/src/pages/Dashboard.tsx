import { useEffect, useMemo, useRef, useState } from 'react';
import { API_BASE, postJson } from '../lib/api';
import { useAuth } from '../auth/AuthProvider';

type VerifyResp = { valid: boolean; subject?: string; expiresAtEpochSeconds?: number };

export default function Dashboard() {
  const { profile, accessToken, logout } = useAuth();
  const [verify, setVerify] = useState<VerifyResp | null>(null);
  const [wsStatus, setWsStatus] = useState<'idle' | 'connecting' | 'open' | 'closed' | 'error'>('idle');
  const wsRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    if (!accessToken) return;
    (async () => {
      try {
        const res = await postJson<VerifyResp>(`${API_BASE}/auth/verify`, { token: accessToken });
        setVerify(res);
      } catch (e) {
        setVerify({ valid: false });
      }
    })();
  }, [accessToken]);

  const expStr = useMemo(() => {
    if (!verify?.expiresAtEpochSeconds) return '-';
    const d = new Date(verify.expiresAtEpochSeconds * 1000);
    return d.toLocaleString();
  }, [verify]);

  const connectWs = () => {
    if (!accessToken) return;
    setWsStatus('connecting');
    const wsUrl = API_BASE.replace(/^http/, 'ws') + `/ws?access_token=${encodeURIComponent(accessToken)}`;
    const ws = new WebSocket(wsUrl);
    wsRef.current = ws;

    ws.onopen = () => setWsStatus('open');
    ws.onclose = () => setWsStatus('closed');
    ws.onerror = () => setWsStatus('error');
    ws.onmessage = (ev) => {
      console.log('WS message:', ev.data);
    };
  };

  const sendWs = () => {
    wsRef.current?.send(JSON.stringify({ type: 'PING', at: Date.now() }));
  };

  return (
    <div style={{
      padding: '2rem',
      fontFamily: 'Segoe UI, sans-serif',
      background: 'linear-gradient(to right, #fdfbfb, #ebedee)',
      minHeight: '100vh',
      color: '#333'
    }}>
      <h2 style={{
        fontSize: '2.5rem',
        marginBottom: '1.5rem',
        background: 'linear-gradient(to right, #00c6ff, #0072ff)',
        WebkitBackgroundClip: 'text',
        WebkitTextFillColor: 'transparent'
      }}>
        Dashboard
      </h2>

      <div style={{
        display: 'flex',
        alignItems: 'center',
        background: '#fff',
        padding: '1rem 1.5rem',
        borderRadius: '12px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
        marginBottom: '2rem'
      }}>
        {profile?.pictureUrl && (
          <img
            alt="avatar"
            src={profile.pictureUrl}
            width={64}
            height={64}
            style={{ borderRadius: '50%', marginRight: '1rem' }}
          />
        )}
        <div>
          <div style={{ fontWeight: 600, fontSize: '1.25rem' }}>{profile?.name}</div>
          <div style={{ color: '#777' }}>{profile?.email}</div>
        </div>
        <div style={{ marginLeft: 'auto' }}>
          <button
            onClick={logout}
            style={{
              backgroundColor: '#ff6b6b',
              color: '#fff',
              border: 'none',
              padding: '0.5rem 1rem',
              borderRadius: '6px',
              fontWeight: 600,
              cursor: 'pointer'
            }}
          >
            Sign out
          </button>
        </div>
      </div>

      <section style={{
        background: 'linear-gradient(to right, #fdfbfb, #ebedee)',
        padding: '1.5rem',
        borderRadius: '12px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
        marginBottom: '2rem'
      }}>
        <h3 style={{
          fontSize: '1.5rem',
          marginBottom: '1rem',
          color: '#333',
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem'
        }}>
          Token Info
        </h3>

        <div style={{
          display: 'grid',
          gridTemplateColumns: '1fr 2fr',
          rowGap: '1rem',
          columnGap: '1rem',
          alignItems: 'center'
        }}>
          <div style={{ fontWeight: 600, color: '#555' }}>Valid</div>
          <div>
            <span style={{
              padding: '0.25rem 0.75rem',
              borderRadius: '6px',
              backgroundColor: verify?.valid ? '#55efc4' : '#ff7675',
              color: '#2d3436',
              fontWeight: 600
            }}>
              {verify?.valid ? 'Yes' : 'No'}
            </span>
          </div>

          <div style={{ fontWeight: 600, color: '#555' }}>Subject (user id)</div>
          <div style={{ color: '#333' }}>{verify?.subject ?? '-'}</div>

          <div style={{ fontWeight: 600, color: '#555' }}>Expires</div>
          <div style={{ color: '#333' }}>{expStr}</div>
        </div>
      </section>
    </div>
  );
}