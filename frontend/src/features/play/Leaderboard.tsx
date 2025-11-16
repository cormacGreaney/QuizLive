import { useEffect, useMemo, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client, IMessage } from '@stomp/stompjs';

type ScoreRow = { userId: string; score: number; rank?: number };

type LeaderboardProps = {
  quizId: number;
  nickname: string; // used as userId
  onReady?: (api: { answer: (questionId: number, selectedOption: number) => void }) => void;
};

const WS_URL = 'http://localhost:8083/ws';



export default function Leaderboard({ quizId, nickname, onReady }: LeaderboardProps) {
  const [connected, setConnected] = useState(false);
  const [participants, setParticipants] = useState(0);
  const [rows, setRows] = useState<ScoreRow[]>([]);

  const onReadyRef = useRef<typeof onReady>();
  onReadyRef.current = onReady;

  useEffect(() => {
    const sock = new SockJS(WS_URL);
    const stomp = new Client({
      webSocketFactory: () => sock as unknown as WebSocket,
      debug: () => {},
      reconnectDelay: 3000,
      heartbeatIncoming: 0,
      heartbeatOutgoing: 0,
      onConnect: () => {
        setConnected(true);

        // Subscribe to leaderboard updates only
        stomp.subscribe(`/topic/quiz/${quizId}/leaderboard`, (msg: IMessage) => {
          try {
            const data = JSON.parse(msg.body);

            // Expecting: { quizId, scores: [{userId, score, rank}], totalParticipants }
            const incoming: ScoreRow[] = Array.isArray(data?.scores)
              ? (data.scores as any[]).map((r) => ({
                userId: String(r?.userId ?? ''),
                score: typeof r?.score === 'number' ? r.score : parseInt(String(r?.score ?? 0), 10) || 0,
                rank: typeof r?.rank === 'number' ? r.rank : undefined,
              })).filter((r) => r.userId.length > 0)
              : [];

            // Sort: prefer backend rank if present; otherwise score desc, then userId
            incoming.sort((a, b) => {
              if (typeof a.rank === 'number' && typeof b.rank === 'number') {
                return a.rank - b.rank;
              }
              const byScore = b.score - a.score;
              return byScore !== 0 ? byScore : a.userId.localeCompare(b.userId);
            });

            // Assign rank if backend didn’t
            const ranked = incoming.map((r, i) => ({
              ...r,
              rank: typeof r.rank === 'number' ? r.rank : i + 1,
            }));

            setRows(ranked);

            const total =
              (typeof data?.totalParticipants === 'number' ? data.totalParticipants : undefined) ??
              (typeof data?.participantsCount === 'number' ? data.participantsCount : undefined) ??
              (typeof data?.participantCount === 'number' ? data.participantCount : undefined) ??
              (typeof data?.total === 'number' ? data.total : 0);
            setParticipants(total || 0);
          } catch {
            // ignore parse errors
          }
        });

        // Announce join
        stomp.publish({
          destination: `/app/quiz/${quizId}/join`,
          headers: { 'content-type': 'text/plain' },
          body: nickname,
        });

        // Expose answer API to parent
        onReadyRef.current?.({
          answer: (questionId: number, selectedOption: number) => {
            if (!stomp.connected) return;
            const payload = { userId: nickname, questionId, selectedOption };
            stomp.publish({
              destination: `/app/quiz/${quizId}/answer`,
              headers: { 'content-type': 'application/json' },
              body: JSON.stringify(payload),
            });
          },
        });
      },
      onDisconnect: () => setConnected(false),
    });

    stomp.activate();
    return () => { stomp.deactivate(); };
  }, [quizId, nickname]);

  const leader = useMemo(() => rows[0] ?? null, [rows]);

  return (
    <div style={styles.wrapper}>
      <div style={styles.headerRow}>
        <h3 style={styles.title}>Live Leaderboard</h3>
        <div style={{ ...styles.status, color: connected ? '#1b5e20' : '#b71c1c' }}>
          {connected ? 'Realtime: connected' : 'Realtime: connecting…'}
        </div>
      </div>

      <div style={styles.metaRow}>
        <div>Participants: <strong>{participants}</strong></div>
      </div>

      {/* Leader strip */}
      <div style={styles.leaderBox}>
        <span style={{ fontSize: 18 }}>🏆</span>
        {leader ? (
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 8 }}>
            <span style={{ fontWeight: 700 }}>
              {leader.userId}{leader.userId === nickname ? ' (you)' : ''}
            </span>
            <span style={{ color: '#555' }}>is leading with</span>
            <span style={{ fontWeight: 700 }}>{leader.score}</span>
          </div>
        ) : (
          <span style={{ color: '#888' }}>—</span>
        )}
      </div>

      {/* Table */}
      <div style={styles.table}>
        <div style={{ ...styles.row, ...styles.header }}>
          <div style={{ width: 64 }}>Rank</div>
          <div style={{ flex: 1 }}>Participant</div>
          <div style={{ width: 100, textAlign: 'right' }}>Score</div>
        </div>

        {rows.length === 0 ? (
          <div style={styles.empty}>Waiting for first answers…</div>
        ) : rows.map((e) => {
          const isYou = e.userId === nickname;
          return (
            <div key={`${e.userId}-${e.rank}`} style={{ ...styles.row, ...(isYou ? styles.you : {}) }}>
              <div style={{ width: 64 }}>{e.rank}</div>
              <div style={{ flex: 1, fontWeight: isYou ? 700 : 500 }}>
                {e.userId}{isYou ? ' (you)' : ''}
              </div>
              <div style={{ width: 100, textAlign: 'right', fontWeight: 600 }}>{e.score}</div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  wrapper: { marginTop: 16, padding: 12, background: '#fafbfc', border: '1px solid #e5e9f0', borderRadius: 8 },
  headerRow: { display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 6 },
  title: { margin: 0, fontSize: 18 },
  status: { fontSize: 12 },
  metaRow: { display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10, fontSize: 13 },
  leaderBox: {
    display: 'flex',
    alignItems: 'center',
    gap: 10,
    padding: '8px 10px',
    background: '#ffffff',
    border: '1px solid #e5e9f0',
    borderRadius: 6,
    marginBottom: 10,
  },
  table: { background: '#fff', border: '1px solid #e5e9f0', borderRadius: 6, overflow: 'hidden' },
  header: { background: '#f2f5f8', fontWeight: 700, color: '#345' },
  row: { display: 'flex', alignItems: 'center', padding: '8px 10px', borderTop: '1px solid #f2f4f7', fontSize: 14 },
  you: { background: '#fffdf4' },
  empty: { padding: 12, textAlign: 'center', color: '#789' },
};
