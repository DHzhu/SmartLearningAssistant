import { describe, it, expect, beforeEach, vi } from 'vitest';
import { login, register, setToken, removeToken } from './api';

const mockFetch = vi.fn();
vi.stubGlobal('fetch', mockFetch);

describe('API Service', () => {
  beforeEach(() => {
    mockFetch.mockReset();
    sessionStorage.clear();
  });

  describe('login', () => {
    it('should send login request and return response', async () => {
      const mockResponse = {
        token: 'jwt-token',
        userId: 1,
        username: 'testuser',
        role: 'ROLE_USER',
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve(mockResponse),
      });

      const result = await login({ username: 'testuser', password: 'pass123' });

      expect(result).toEqual(mockResponse);
      expect(mockFetch).toHaveBeenCalledWith('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: 'testuser', password: 'pass123' }),
      });
    });

    it('should throw on non-ok response', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 401,
        statusText: 'Unauthorized',
      });

      await expect(
        login({ username: 'testuser', password: 'wrong' })
      ).rejects.toThrow('HTTP 401');
    });
  });

  describe('register', () => {
    it('should send register request', async () => {
      const mockResponse = {
        token: 'jwt-token',
        userId: 2,
        username: 'newuser',
        role: 'ROLE_USER',
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve(mockResponse),
      });

      const result = await register({ username: 'newuser', password: 'pass123' });
      expect(result).toEqual(mockResponse);
    });
  });

  describe('token management', () => {
    it('should store and retrieve token', () => {
      setToken('test-token');
      expect(sessionStorage.getItem('jwt_token')).toBe('test-token');
    });

    it('should remove token', () => {
      setToken('test-token');
      removeToken();
      expect(sessionStorage.getItem('jwt_token')).toBeNull();
    });
  });
});
