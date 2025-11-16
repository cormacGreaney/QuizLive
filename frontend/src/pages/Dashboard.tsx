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
      color: '#333',
      position: 'relative',
      animation: 'fadeIn 0.6s ease-in'
    }}>
      <div style={{
        background: 'linear-gradient(to right, #a1c4fd, #c2e9fb)',
        padding: '2rem',
        borderRadius: '16px',
        marginBottom: '2rem',
        textAlign: 'center',
        color: '#1e3a8a',
        fontWeight: 600,
        fontSize: '1.8rem',
        boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
        animation: 'slideDown 0.5s ease-out'
      }}>
        Welcome back to <span style={{ fontWeight: 700 }}>QuizLive</span>!
        <div style={{ fontSize: '1rem', marginTop: '0.5rem', color: '#1e3a8a' }}>
          Ready to host, manage, or explore your quizzes.
        </div>
      </div>

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
        padding: '1.5rem',
        borderRadius: '12px',
        background: 'white',
        border: '3px solid',
        borderImage: 'linear-gradient(to right, #74ebd5, #9face6) 1',
        boxShadow: '0 4px 12px rgba(0,0,0,0.06)',
        animation: 'gradientBorder 6s linear infinite'
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
          <div style={{
            width: 10,
            height: 10,
            borderRadius: '50%',
            backgroundColor:
              wsStatus === 'open' ? '#10b981' :
              wsStatus === 'connecting' ? '#facc15' :
              wsStatus === 'error' ? '#ef4444' :
              '#9ca3af',
            animation: 'pulseDot 1.5s infinite'
          }} />
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
              fontWeight: 600,
              transition: 'transform 0.3s ease',
              animation: verify?.valid ? 'pulseGreen 1.5s infinite' : 'pulseRed 1.5s infinite'
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

      <div style={{
        fontStyle: 'italic',
        color: '#555',
        marginTop: '3rem',
        textAlign: 'center',
        fontSize: '1rem'
      }}>
        “Learning never exhausts the mind.” — Leonardo da Vinci
      </div>

      <div style={{
        position: 'absolute',
        top: '-60px',
        right: '-60px',
        width: '200px',
        height: '200px',
        background: 'radial-gradient(circle at center, #c2e9fb 0%, transparent 70%)',
        borderRadius: '50%',
        zIndex: 0
      }} />

      <style>
        {`
          @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
          }
          @keyframes slideDown {
            from { transform: translateY(-20px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
          }
          @keyframes pulseGreen {
            0% { transform: scale(1); }
            50% { transform: scale(1.05); }
            100% { transform: scale(1); }
          }
          @keyframes pulseRed {
            0% { transform: scale(1); }
            50% { transform: scale(1.05); }
            100% { transform: scale(1); }
          }
          @keyframes pulseDot {
            0% { transform: scale(1); opacity: 0.8; }
            50% { transform: scale(1.3); opacity: 1; }
            100% { transform: scale(1); opacity: 0.8; }
          }
          @keyframes gradientBorder {
            0% { border-image-source: linear-gradient(to right, #74ebd5, #9face
                      }
        `}
      </style>
    </div>
  );
}