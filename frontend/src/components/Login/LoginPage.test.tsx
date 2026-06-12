import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { LoginPage } from './LoginPage';
import { AuthProvider } from '../../context/AuthContext';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useNavigate: () => mockNavigate };
});

function renderLoginPage() {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <LoginPage />
      </AuthProvider>
    </BrowserRouter>
  );
}

describe('LoginPage', () => {
  beforeEach(() => {
    mockNavigate.mockReset();
    sessionStorage.clear();
  });

  it('should render login form', () => {
    renderLoginPage();

    expect(screen.getByLabelText(/用户名/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/密码/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /登录/i })).toBeInTheDocument();
  });

  it('should show error on invalid credentials', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValueOnce({
      ok: false,
      status: 401,
      statusText: 'Unauthorized',
    }));

    renderLoginPage();

    fireEvent.change(screen.getByLabelText(/用户名/i), {
      target: { value: 'testuser' },
    });
    fireEvent.change(screen.getByLabelText(/密码/i), {
      target: { value: 'wrongpass' },
    });
    fireEvent.click(screen.getByRole('button', { name: /登录/i }));

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent(/用户名或密码错误/i);
    });
  });

  it('should navigate to home on successful login', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve({
        token: 'jwt-token',
        userId: 1,
        username: 'testuser',
        role: 'ROLE_USER',
      }),
    }));

    renderLoginPage();

    fireEvent.change(screen.getByLabelText(/用户名/i), {
      target: { value: 'testuser' },
    });
    fireEvent.change(screen.getByLabelText(/密码/i), {
      target: { value: 'password123' },
    });
    fireEvent.click(screen.getByRole('button', { name: /登录/i }));

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/');
    });
  });
});
