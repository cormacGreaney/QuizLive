import { Route, Routes, Navigate, Link } from 'react-router-dom';
import Login from './pages/Login';
import Callback from './pages/Callback';
import Dashboard from './pages/Dashboard';
import { useAuth } from './auth/AuthProvider';
import AuthProvider from './auth/AuthProvider';

function Protected({ children }: { children: JSX.Element }) {
  const { accessToken } = useAuth();
  if (!accessToken) return <Navigate to="/login" replace />;
  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <nav style={{ padding: '8px', borderBottom: '1px solid #ddd', marginBottom: 16 }}>
        <Link to="/" style={{ marginRight: 12 }}>Home</Link>
        <Link to="/dashboard">Dashboard</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/oauth2/callback/google" element={<Callback />} />
        <Route path="/dashboard" element={<Protected><Dashboard /></Protected>} />
      </Routes>
    </AuthProvider>
  );
}
