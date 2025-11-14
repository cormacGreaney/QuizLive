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
  const text = await res.text();
  if (!text) {
    return undefined as T;
  }
  return JSON.parse(text) as T;
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
  options: string[];
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

export async function deleteQuiz(quizId: number) {
  return http<void>(`${QMS}/quizzes/${quizId}`, { method: 'DELETE' });
}

export async function updateQuestion(params: {
  quizId: number;
  questionId: number;
  questionText: string;
  options: string[];
  correctOption: number;
}) {
  const { quizId, questionId, ...body } = params;
  return http<import('./types').Quiz>(`${QMS}/quizzes/${quizId}/questions/${questionId}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  });
}

export async function deleteQuestion(quizId: number, questionId: number) {
  return http<void>(`${QMS}/quizzes/${quizId}/questions/${questionId}`, { method: 'DELETE' });
}

export async function getQuizById(id: number) {
  const res = await fetch(`${API_BASE}/qms/api/quizzes/${id}`);
  if (!res.ok) throw new Error(`Failed to fetch quiz ${id}`);
  return res.json();
}

export const API = {
  getQuizById,
};
