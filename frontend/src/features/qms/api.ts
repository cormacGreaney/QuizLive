const API_BASE = import.meta.env.VITE_API_BASE as string; // e.g. http://localhost:8080
const QMS = `${API_BASE}/qms/api`;

import type { Quiz } from './types';

let tokenProvider: (() => string | null) | null = null;
export function setTokenProvider(fn: () => string | null) {
  tokenProvider = fn;
}

// DEBUG helper (safe): allows to run window.__dbg_getToken() in console
if (typeof window !== 'undefined') {
  // @ts-ignore
  window.__dbg_getToken = () => (tokenProvider ? tokenProvider() : localStorage.getItem('accessToken'));
}

async function http<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
  // IMPORTANT: fallback ensures Authorization is sent even if provider wasn't wired yet
  const provided = tokenProvider ? tokenProvider() : null;
  const token = provided ?? (typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null);

  const res = await fetch(input, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    credentials: 'include',
    ...init,
  });

  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`${res.status} ${res.statusText} :: ${text}`);
  }

  const text = await res.text();
  if (!text) return undefined as T;
  try { return JSON.parse(text) as T; } catch { return text as unknown as T; }
}

export async function listQuizzes() {
  return http<Quiz[]>(`${QMS}/quizzes`);
}

export async function createQuiz(input: { title: string; description: string }) {
  return http<Quiz>(`${QMS}/quizzes`, { method: 'POST', body: JSON.stringify(input) });
}

export async function addQuestion(params: {
  quizId: number;
  questionText: string;
  options: string[];
  correctOption: number;
}) {
  const { quizId, ...body } = params;
  return http<Quiz>(`${QMS}/quizzes/${quizId}/questions`, {
    method: 'POST',
    body: JSON.stringify(body),
  });
}

export async function startQuiz(quizId: number) {
  return http<Quiz>(`${QMS}/quizzes/${quizId}/start`, { method: 'POST' });
}

export async function endQuiz(quizId: number) {
  return http<Quiz>(`${QMS}/quizzes/${quizId}/end`, { method: 'POST' });
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
  return http<Quiz>(`${QMS}/quizzes/${quizId}/questions/${questionId}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  });
}

export async function deleteQuestion(quizId: number, questionId: number) {
  return http<void>(`${QMS}/quizzes/${quizId}/questions/${questionId}`, { method: 'DELETE' });
}

export async function getQuizById(id: number) {
  return http<Quiz>(`${QMS}/quizzes/${id}`);
}

export const API = {
  listQuizzes,
  createQuiz,
  addQuestion,
  startQuiz,
  endQuiz,
  deleteQuiz,
  updateQuestion,
  deleteQuestion,
  getQuizById,
};
