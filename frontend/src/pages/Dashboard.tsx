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
    <div style={{ padding: 16 }}>
      <h2>Dashboard</h2>
      <div style={{ display: 'flex', gap: 24, alignItems: 'center' }}>
        {profile?.pictureUrl && <img alt="avatar" src={profile.pictureUrl} width={48} height={48} style={{ borderRadius: '50%' }}/>}
        <div>
          <div><b>{profile?.name}</b></div>
          <div style={{ color: '#666' }}>{profile?.email}</div>
        </div>
        <div style={{ marginLeft: 'auto' }}>
          <button onClick={logout}>Sign out</button>
        </div>
      </div>

      <h3 style={{ marginTop: 24 }}>Token</h3>
      <table>
        <tbody>
          <tr><td>Valid</td><td>{verify?.valid ? 'Yes' : 'No'}</td></tr>
          <tr><td>Subject (user id)</td><td>{verify?.subject ?? '-'}</td></tr>
          <tr><td>Expires</td><td>{expStr}</td></tr>
        </tbody>
      </table>

      <h3 style={{ marginTop: 24 }}>Realtime (RTS)</h3>
      <p>Connect WS via gateway on <code>/ws</code> using your bearer token.</p>
      <div style={{ display: 'flex', gap: 8 }}>
        <button onClick={connectWs} disabled={wsStatus==='open'}>Connect</button>
        <button onClick={sendWs} disabled={wsStatus!=='open'}>Send message</button>
        <span>Status: {wsStatus}</span>
      </div>

      <h3 style={{ marginTop: 24 }}>QMS example</h3>
      <p>Once QMS confirms endpoints, we’ll list quizzes here.</p>
    </div>
  );
}
