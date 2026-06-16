import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Navbar } from './components/Common/Navbar';
import { LoginPage } from './components/Login/LoginPage';
import { RequireAuth } from './components/Common/RequireAuth';
import { KnowledgePage } from './components/Knowledge/KnowledgePage';
import { BillingPage } from './components/Billing/BillingPage';
import { ChatPage } from './components/Chat/ChatPage';

function Dashboard() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>Dashboard</h1>
      <p>Welcome to Smart Learning Assistant</p>
    </div>
  );
}

function AdminPage() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>Admin Panel</h1>
      <p>Admin-only content</p>
    </div>
  );
}

function AppLayout() {
  return (
    <>
      <Navbar />
      <main style={{ paddingTop: '70px' }}>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route
            path="/admin"
            element={
              <RequireAuth roles={['ROLE_ADMIN']}>
                <AdminPage />
              </RequireAuth>
            }
          />
          <Route
            path="/knowledge"
            element={
              <RequireAuth roles={['ROLE_ADMIN']}>
                <KnowledgePage />
              </RequireAuth>
            }
          />
          <Route
            path="/billing"
            element={
              <RequireAuth>
                <BillingPage />
              </RequireAuth>
            }
          />
          <Route
            path="/chat"
            element={
              <RequireAuth>
                <ChatPage />
              </RequireAuth>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </>
  );
}

export function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/*" element={<RequireAuth><AppLayout /></RequireAuth>} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}