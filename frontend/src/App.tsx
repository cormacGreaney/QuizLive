import { Route, Routes, Navigate, Link } from 'react-router-dom';
import Login from './pages/Login';
import Callback from './pages/Callback';
import Dashboard from './pages/Dashboard';
import { useAuth } from './auth/AuthProvider';
import AuthProvider from './auth/AuthProvider';
import AdminQuizzes from './features/qms/AdminQuizzes';
import ParticipantPlay from './features/play/ParticipantPlay';

function Protected({ children }: { children: JSX.Element }) {
  const { accessToken } = useAuth();
  if (!accessToken) return <Navigate to="/login" replace />;
  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <nav style={{
        padding: '1rem 2rem',
        marginBottom: '2rem',
        background: 'linear-gradient(to right, #74ebd5, #9face6)',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderRadius: '0 0 12px 12px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
        fontFamily: 'Segoe UI, sans-serif'
      }}>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <Link to="/dashboard" style={navLinkStyle}>Dashboard</Link>
          <Link to="/admin/quizzes" style={navLinkStyle}>Quiz Manager</Link>
        </div>
        <div style={{
          fontSize: '1.4rem',
          fontWeight: 700,
          color: '#333',
          letterSpacing: '0.5px'
        }}>
          QuizLive
        </div>
      </nav>

      <Routes>
        <Route path="/admin/quizzes" element={<Protected><AdminQuizzes /></Protected>} />
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/oauth2/callback/google" element={<Callback />} />
        <Route path="/dashboard" element={<Protected><Dashboard /></Protected>} />
        <Route path="/play/:quizId" element={<ParticipantPlay />} />
      </Routes>
    </AuthProvider>
  );
}

const navLinkStyle = {
  backgroundColor: '#ffffffaa',
  padding: '0.5rem 1rem',
  borderRadius: '8px',
  textDecoration: 'none',
  color: '#333',
  fontWeight: 600,
  transition: 'all 0.3s ease',
  boxShadow: '0 2px 6px rgba(0,0,0,0.05)'
};