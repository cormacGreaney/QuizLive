import { useEffect, useMemo, useState } from 'react';
import type { Quiz } from './types';
import { listQuizzes, createQuiz, addQuestion, startQuiz, endQuiz, deleteQuiz } from './api';

type NewQuizForm = { title: string; description: string };
type NewQuestionForm = { questionText: string; options: string[]; correctOption: number };

export default function AdminQuizzes() {
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [form, setForm] = useState<NewQuizForm>({ title: '', description: '' });

  const [questionForm, setQuestionForm] = useState<NewQuestionForm>({
    questionText: '',
    options: ['', ''],
    correctOption: 1,
  });
  const [questionForQuizId, setQuestionForQuizId] = useState<number | null>(null);

  const totalDraft = useMemo(() => quizzes.filter(q => q.status === 'DRAFT').length, [quizzes]);
  const totalLive = useMemo(() => quizzes.filter(q => q.status === 'LIVE').length, [quizzes]);
  const totalEnded = useMemo(() => quizzes.filter(q => q.status === 'ENDED').length, [quizzes]);

  async function refresh() {
    setLoading(true);
    setError(null);
    try {
      const data = await listQuizzes();
      setQuizzes(Array.isArray(data) ? data : [data].filter(Boolean)); // tolerate single-object returns
    } catch (e: any) {
      setError(e?.message || 'Failed to load quizzes');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    refresh();
  }, []);

  async function onCreateQuiz(e: React.FormEvent) {
    e.preventDefault();
    if (!form.title.trim()) {
      setError('Title is required');
      return;
    }
    try {
      setLoading(true);
      setError(null);
      await createQuiz(form);
      setForm({ title: '', description: '' });
      await refresh();
    } catch (e: any) {
      setError(e?.message || 'Failed to create quiz');
    } finally {
      setLoading(false);
    }
  }

  async function onAddQuestion(e: React.FormEvent) {
    e.preventDefault();
    if (!questionForQuizId) return;
    if (!questionForm.questionText.trim()) {
      setError('Question text is required');
      return;
    }
    if (questionForm.options.some(opt => !opt.trim())) {
      setError('All option fields must be filled');
      return;
    }
    try {
      setLoading(true);
      setError(null);
      await addQuestion({
        quizId: questionForQuizId,
        questionText: questionForm.questionText,
        options: questionForm.options,
        correctOption: Number(questionForm.correctOption),
      });
      setQuestionForm({ questionText: '', options: ['', ''], correctOption: 1 });
      setQuestionForQuizId(null);
      await refresh();
    } catch (e: any) {
      setError(e?.message || 'Failed to add question');
    } finally {
      setLoading(false);
    }
  }

  async function onStart(quizId: number) {
    try {
      setLoading(true);
      setError(null);
      await startQuiz(quizId);
      await refresh();
    } catch (e: any) {
      setError(e?.message || 'Failed to start quiz');
    } finally {
      setLoading(false);
    }
  }

  async function onEnd(quizId: number) {
    try {
      setLoading(true);
      setError(null);
      await endQuiz(quizId);
      await refresh();
    } catch (e: any) {
      setError(e?.message || 'Failed to end quiz');
    } finally {
      setLoading(false);
    }
  }

  async function onDelete(quizId: number) {
    if (!confirm('Are you sure you want to delete this quiz? This cannot be undone.')) {
      return;
    }
    try {
      setLoading(true);
      setError(null);
      await deleteQuiz(quizId);
      await refresh();
    } catch (e: any) {
      setError(e?.message || 'Failed to delete quiz');
    } finally {
      setLoading(false);
    }
  }

  function copyLink(quizId: number) {
  const url = `${window.location.origin}/play/${quizId}`;
  navigator.clipboard.writeText(url)
    .then(() => alert(`Link copied:\n${url}`))
    .catch(() => alert(`Copy failed. Link:\n${url}`));
}


  return (
    <div style={{ maxWidth: 980, margin: '24px auto', padding: 16, fontFamily: 'system-ui, sans-serif' }}>
      <h1 style={{ marginBottom: 4 }}>Quiz Manager</h1>
      <p style={{ marginTop: 0, color: '#555' }}>
        Create quizzes, add questions, and move them through DRAFT → LIVE → ENDED.
      </p>

      <section style={{ display: 'flex', gap: 16, margin: '16px 0' }}>
        <Stat label="Total" value={quizzes.length} />
        <Stat label="Draft" value={totalDraft} />
        <Stat label="Live" value={totalLive} />
        <Stat label="Ended" value={totalEnded} />
      </section>

      {error && (
        <div style={{ background: '#fee2e2', color: '#991b1b', padding: 12, borderRadius: 8, marginBottom: 12 }}>
          {error}
        </div>
      )}

      <form onSubmit={onCreateQuiz} style={cardStyle}>
        <h2 style={{ marginTop: 0 }}>New Quiz</h2>
        <div style={row}>
          <label style={label}>Title</label>
          <input
            style={input}
            value={form.title}
            onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
            placeholder="e.g. Geography Round 1"
          />
        </div>
        <div style={row}>
          <label style={label}>Description</label>
          <input
            style={input}
            value={form.description}
            onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
            placeholder="Optional description"
          />
        </div>
        <button style={primaryBtn} disabled={loading} type="submit">
          {loading ? 'Saving...' : 'Create Quiz'}
        </button>
      </form>

      <div style={{ ...cardStyle, marginTop: 16 }}>
        <h2 style={{ marginTop: 0 }}>Quizzes</h2>
        {loading && <div>Loading…</div>}
        {!loading && quizzes.length === 0 && <div>No quizzes yet.</div>}
        {!loading && quizzes.length > 0 && (
          <table style={table}>
            <thead>
              <tr>
                <th style={th}>ID</th>
                <th style={th}>Title</th>
                <th style={th}>Status</th>
                <th style={th}>Questions</th>
                <th style={th}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {quizzes.map(q => (
                <tr key={q.id}>
                  <td style={td}>{q.id}</td>
                  <td style={td}>{q.title}</td>
                  <td style={td}>{q.status}</td>
                  <td style={td}>{q.questions?.length ?? 0}</td>
                  <td style={td}>
                    <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                      <button style={btn} onClick={() => setQuestionForQuizId(q.id)}>Add Question</button>
                      <button style={btn} onClick={() => onStart(q.id)} disabled={q.status !== 'DRAFT'}>
                        Start
                      </button>
                      <button style={btn} onClick={() => onEnd(q.id)} disabled={q.status !== 'LIVE'}>
                        End
                      </button>
                      <button style={{ ...btn, borderColor: '#dc2626', color: '#dc2626' }} onClick={() => onDelete(q.id)}>
                        Delete
                      </button>
                      <button onClick={() => copyLink(q.id)}>Copy Participant Link</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {questionForQuizId !== null && (
        <form onSubmit={onAddQuestion} style={{ ...cardStyle, marginTop: 16 }}>
          <h3 style={{ marginTop: 0 }}>Add Question to Quiz #{questionForQuizId}</h3>
          <div style={row}>
            <label style={label}>Question Text</label>
            <input
              style={input}
              value={questionForm.questionText}
              onChange={e => setQuestionForm(f => ({ ...f, questionText: e.target.value }))}
              placeholder="e.g. Capital of Ireland?"
            />
          </div>

          <div style={{ marginBottom: 16 }}>
            <label style={{ ...label, display: 'block', marginBottom: 8 }}>Options</label>
            {questionForm.options.map((option, idx) => (
              <div key={idx} style={{ display: 'flex', gap: 8, marginBottom: 8, alignItems: 'center' }}>
                <input
                  style={{ ...input, flex: 1 }}
                  value={option}
                  onChange={e => {
                    const newOptions = [...questionForm.options];
                    newOptions[idx] = e.target.value;
                    setQuestionForm(f => ({ ...f, options: newOptions }));
                  }}
                  placeholder={`Option ${idx + 1}`}
                />
              </div>
            ))}
            {questionForm.options.length < 4 && (
              <button
                type="button"
                style={btn}
                onClick={() => setQuestionForm(f => ({ ...f, options: [...f.options, ''] }))}
              >
                + Add Option
              </button>
            )}
          </div>

          <div style={row}>
            <label style={label}>Correct Option</label>
            <select
              style={input}
              value={questionForm.correctOption}
              onChange={e => setQuestionForm(f => ({ ...f, correctOption: Number(e.target.value) }))}
            >
              {questionForm.options.map((_, idx) => (
                <option key={idx} value={idx + 1}>
                  Option {idx + 1}
                </option>
              ))}
            </select>
          </div>

          <div style={{ display: 'flex', gap: 8 }}>
            <button style={primaryBtn} disabled={loading} type="submit">
              {loading ? 'Adding…' : 'Add Question'}
            </button>
            <button style={btn} type="button" onClick={() => setQuestionForQuizId(null)}>Cancel</button>
          </div>
        </form>
      )}
    </div>
  );
}

function Stat({ label, value }: { label: string; value: number }) {
  return (
    <div style={{ padding: 12, borderRadius: 8, background: '#f9fafb', minWidth: 120, border: '1px solid #e5e7eb' }}>
      <div style={{ color: '#6b7280', fontSize: 12 }}>{label}</div>
      <div style={{ fontSize: 22, fontWeight: 700 }}>{value}</div>
    </div>
  );
}

const cardStyle: React.CSSProperties = {
  border: '1px solid #e5e7eb',
  borderRadius: 12,
  padding: 16,
  background: 'white',
};

const row: React.CSSProperties = { display: 'grid', gridTemplateColumns: '140px 1fr', alignItems: 'center', gap: 12, marginBottom: 12 };
const label: React.CSSProperties = { color: '#374151', fontSize: 14 };
const input: React.CSSProperties = { padding: '8px 10px', border: '1px solid #d1d5db', borderRadius: 8 };
const btn: React.CSSProperties = { padding: '6px 10px', border: '1px solid #d1d5db', borderRadius: 8, background: '#fff', cursor: 'pointer' };
const primaryBtn: React.CSSProperties = { ...btn, background: '#111827', color: 'white', borderColor: '#111827' };
const table: React.CSSProperties = { width: '100%', borderCollapse: 'collapse' };
const th: React.CSSProperties = { textAlign: 'left', borderBottom: '1px solid #e5e7eb', padding: '8px 6px', fontWeight: 600, fontSize: 14, color: '#374151' };
const td: React.CSSProperties = { borderBottom: '1px solid #f3f4f6', padding: '8px 6px', fontSize: 14, color: '#111827' };
