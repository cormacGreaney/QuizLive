import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { API } from '../qms/api';

type QuizStatus = 'DRAFT' | 'LIVE' | 'ENDED';

type Question = {
  id: number;
  questionText: string;
  // make this flexible; we’ll normalize at render time
  options?: unknown;
  correctOption: number;
};

type Quiz = {
  id: number;
  title: string;
  description: string;
  status: QuizStatus;
  questions: Question[];
};

// Turn whatever we got into string options safely
function normalizeOptions(raw: unknown): string[] {
  if (!raw) return [];

  // Already an array of strings
  if (Array.isArray(raw) && raw.every(v => typeof v === 'string')) {
    return raw as string[];
  }

  // Array of objects like [{text:"..."}, {value:"..."}]
  if (
    Array.isArray(raw) &&
    raw.every(v => typeof v === 'object' && v != null)
  ) {
    return (raw as any[]).map((o) => {
      if (typeof o?.text === 'string') return o.text;
      if (typeof o?.optionText === 'string') return o.optionText;
      if (typeof o?.value === 'string') return o.value;
      return String(o);
    });
  }

  // Object map like {"0": "A", "1": "B"} (not an array)
  if (typeof raw === 'object') {
    const vals = Object.values(raw as Record<string, unknown>);
    if (vals.every(v => typeof v === 'string')) return vals as string[];
    return vals.map(v => (typeof v === 'string' ? v : String(v)));
  }

  // Pipe/comma separated string
  if (typeof raw === 'string') {
    const parts = raw.split('|').length > 1 ? raw.split('|') : raw.split(',');
    return parts.map(s => s.trim()).filter(Boolean);
  }

  return [];
}

export default function ParticipantPlay() {
  const { quizId } = useParams();

  const [loading, setLoading] = useState(true);
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [nickname, setNickname] = useState('');
  const [acceptedName, setAcceptedName] = useState<string | null>(null);

  // simple “current question” pointer (client-side only for now)
  const [index, setIndex] = useState(0);
  const current = useMemo(() => (quiz?.questions ?? [])[index], [quiz, index]);

  async function fetchQuiz() {
    try {
      setError(null);
      const data = await API.getQuizById(Number(quizId)); // GET /qms/api/quizzes/{id}
      setQuiz(data);
    } catch (e: any) {
      setError(e?.message ?? 'Failed to fetch quiz');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    // initial fetch
    fetchQuiz();

    // Poll every 3s until it goes LIVE, then stop
    const timer = setInterval(() => {
      if (!quiz || quiz.status !== 'LIVE') {
        fetchQuiz();
      } else {
        clearInterval(timer);
      }
    }, 3000);

    return () => clearInterval(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [quizId]);

  function handleJoin() {
    if (!nickname.trim()) return;
    setAcceptedName(nickname.trim());
  }

  function handleAnswer(optionIndex: number) {
    if (!quiz) return;
    if (index + 1 < quiz.questions.length) {
      setIndex(index + 1);
    } else {
      alert('Thanks! You’ve finished this quiz.');
    }
  }

  if (loading) return <div style={{ padding: 16 }}>Loading…</div>;
  if (error)   return <div style={{ padding: 16, color: 'crimson' }}>Error: {error}</div>;
  if (!quiz)   return <div style={{ padding: 16 }}>Quiz not found.</div>;

  // helpful debug once per render when we have a current question
  if (current) {
    // eslint-disable-next-line no-console
    console.debug('Current question raw options:', current.options);
  }

  return (
  <div style={{
    maxWidth: 720,
    margin: '40px auto',
    padding: '2rem',
    fontFamily: 'Segoe UI, sans-serif',
    background: 'white',
    borderRadius: 12,
    boxShadow: '0 8px 24px rgba(0,0,0,0.08)'
  }}>
    <h1 style={{
      fontSize: '1.8rem',
      marginBottom: 8,
      background: 'linear-gradient(to right, #00c6ff, #0072ff)',
      WebkitBackgroundClip: 'text',
      WebkitTextFillColor: 'transparent'
    }}>
      {quiz.title}
    </h1>
    <p style={{ color: '#555', marginBottom: 24 }}>{quiz.description}</p>

    {!acceptedName && (
      <div style={{ marginBottom: 32 }}>
        <label style={{ fontSize: 14, color: '#333' }}>
          Enter a nickname:
          <input
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            placeholder="e.g., Cormac"
            style={{
              marginLeft: 8,
              padding: '8px 12px',
              borderRadius: 8,
              border: '1px solid #ccc',
              fontSize: 14
            }}
          />
        </label>
        <button
          onClick={handleJoin}
          style={{
            marginLeft: 12,
            padding: '8px 14px',
            borderRadius: 8,
            border: 'none',
            background: '#111827',
            color: 'white',
            fontWeight: 500,
            cursor: 'pointer'
          }}
        >
          Join
        </button>
      </div>
    )}

    {quiz.status === 'DRAFT' && (
      <div style={{
        padding: 16,
        background: '#fff8e1',
        border: '1px solid #ffe082',
        borderRadius: 8,
        color: '#7c6f00'
      }}>
        Waiting for the host to start… (auto-refreshing)
      </div>
    )}

    {quiz.status === 'LIVE' && acceptedName && current && (
      <div>
        <div style={{ marginBottom: 12, fontSize: 14, color: '#666' }}>
          Playing as <strong>{acceptedName}</strong>
        </div>
        <h2 style={{ marginBottom: 12 }}>
          Question {index + 1} of {quiz.questions.length}
        </h2>
        <p style={{ fontSize: 18, marginBottom: 16 }}>{current.questionText}</p>

        {(() => {
          const optionList = normalizeOptions(current.options);
          if (optionList.length === 0) {
            return (
              <div style={{
                padding: 16,
                background: '#fff8e1',
                border: '1px solid #ffe082',
                borderRadius: 8,
                color: '#7c6f00'
              }}>
                No answer options available for this question yet.
              </div>
            );
          }
          return (
            <div style={{ display: 'grid', gap: 12 }}>
              {optionList.map((opt, i) => (
                <button
                  key={i}
                  onClick={() => handleAnswer(i)}
                  style={{
                    padding: '12px 16px',
                    borderRadius: 8,
                    border: '1px solid #d1d5db',
                    background: '#f9fafb',
                    textAlign: 'left',
                    fontSize: 15,
                    cursor: 'pointer',
                    transition: 'background 0.2s',
                  }}
                  onMouseOver={(e) => (e.currentTarget.style.background = '#f3f4f6')}
                  onMouseOut={(e) => (e.currentTarget.style.background = '#f9fafb')}
                >
                  {opt}
                </button>
              ))}
            </div>
          );
        })()}
      </div>
    )}

    {quiz.status === 'ENDED' && (
      <div style={{
        padding: 16,
        background: '#e8f5e9',
        border: '1px solid #c8e6c9',
        borderRadius: 8,
        color: '#256029'
      }}>
        This quiz has ended. Thanks for playing!
      </div>
    )}
  </div>
);
}
