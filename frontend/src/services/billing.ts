import type { BillingLog } from '../types';

const BASE_URL = '/api/billing';

function getHeaders(): Record<string, string> {
  const token = sessionStorage.getItem('jwt_token');
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  return headers;
}

export async function getBalance(): Promise<{ balance: number }> {
  const response = await fetch(`${BASE_URL}/balance`, { headers: getHeaders() });
  if (!response.ok) throw new Error('Failed to get balance');
  return response.json();
}

export async function recharge(amount: number): Promise<{ balance: number }> {
  const response = await fetch(`${BASE_URL}/recharge?amount=${amount}`, {
    method: 'POST',
    headers: getHeaders(),
  });
  if (!response.ok) throw new Error('Failed to recharge');
  return response.json();
}

export async function getBillingHistory(): Promise<BillingLog[]> {
  const response = await fetch(`${BASE_URL}/history`, { headers: getHeaders() });
  if (!response.ok) throw new Error('Failed to get history');
  return response.json();
}
