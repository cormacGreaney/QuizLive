const API_BASE = import.meta.env.VITE_API_BASE as string; // e.g. http://localhost:8080
const QMS = `${API_BASE}/qms/api`;

async function http<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
  const res = await fetch(input, {
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include', // safe default for gateway cookies if any
    ...init,
  });
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`${res.status} ${res.statusText} :: ${text}`);
  }
  return (await res.json()) as T;
}

export async function listQuizzes() {
  return http<import('./types').Quiz[]>(`${QMS}/quizzes`);
}

export async function createQuiz(input: { title: string; description: string }) {
  return http<import('./types').Quiz>(`${QMS}/quizzes`, {
    method: 'POST',
    body: JSON.stringify(input),
  });
}

export async function addQuestion(params: {
  quizId: number;
  questionText: string;
  correctOption: number;
}) {
  const { quizId, ...body } = params;
  return http<import('./types').Quiz>(`${QMS}/quizzes/${quizId}/questions`, {
    method: 'POST',
    body: JSON.stringify(body),
  });
}

export async function startQuiz(quizId: number) {
  return http<import('./types').Quiz>(`${QMS}/quizzes/${quizId}/start`, { method: 'POST' });
}

export async function endQuiz(quizId: number) {
  return http<import('./types').Quiz>(`${QMS}/quizzes/${quizId}/end`, { method: 'POST' });
}
