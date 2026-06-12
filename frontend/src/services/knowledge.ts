import type { KnowledgeTask } from '../types';

const BASE_URL = '/api/knowledge';

function getHeaders(): Record<string, string> {
  const token = sessionStorage.getItem('jwt_token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

export async function getUploadUrl(filename: string): Promise<{ uploadUrl: string; objectKey: string }> {
  const response = await fetch(
    `${BASE_URL}/upload-url?filename=${encodeURIComponent(filename)}`,
    { method: 'POST', headers: getHeaders() }
  );
  if (!response.ok) throw new Error('Failed to get upload URL');
  return response.json();
}

export async function createTask(filename: string, s3Url: string): Promise<KnowledgeTask> {
  const response = await fetch(
    `${BASE_URL}/tasks?filename=${encodeURIComponent(filename)}&s3Url=${encodeURIComponent(s3Url)}`,
    { method: 'POST', headers: getHeaders() }
  );
  if (!response.ok) throw new Error('Failed to create task');
  return response.json();
}

export async function listTasks(): Promise<KnowledgeTask[]> {
  const response = await fetch(`${BASE_URL}/tasks`, { headers: getHeaders() });
  if (!response.ok) throw new Error('Failed to list tasks');
  return response.json();
}

export async function getTask(taskId: number): Promise<KnowledgeTask> {
  const response = await fetch(`${BASE_URL}/tasks/${taskId}`, { headers: getHeaders() });
  if (!response.ok) throw new Error('Task not found');
  return response.json();
}
