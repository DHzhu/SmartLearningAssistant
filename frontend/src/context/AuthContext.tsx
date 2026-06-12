import { createContext, useContext, useState, useCallback, type ReactNode } from 'react';
import type { User, LoginRequest } from '../types';
import { login as apiLogin, setToken, removeToken } from '../services/api';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(() => {
    const stored = sessionStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  const login = useCallback(async (credentials: LoginRequest) => {
    const response = await apiLogin(credentials);
    setToken(response.token);
    const userData: User = {
      userId: response.userId,
      username: response.username,
      role: response.role,
    };
    setUser(userData);
    sessionStorage.setItem('user', JSON.stringify(userData));
  }, []);

  const logout = useCallback(() => {
    removeToken();
    sessionStorage.removeItem('user');
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: user !== null, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
