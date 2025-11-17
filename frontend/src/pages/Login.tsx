const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID as string;

export default function Login() {
  const handleGoogle = () => {
    const redirectUri = `${window.location.origin}/oauth2/callback/google`;
    const params = new URLSearchParams({
      client_id: GOOGLE_CLIENT_ID,
      redirect_uri: redirectUri,
      response_type: 'code',
      scope: 'openid email profile',
      prompt: 'consent',
      access_type: 'online',
    });
    const url = `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`;
    window.location.href = url;
  };

  return (
    <div style={{
      maxWidth: 420,
      margin: '80px auto',
      padding: '2rem',
      borderRadius: 12,
      boxShadow: '0 8px 24px rgba(0,0,0,0.08)',
      background: 'white',
      fontFamily: 'Segoe UI, sans-serif',
      textAlign: 'center'
    }}>
      <h1 style={{
        fontSize: '2rem',
        marginBottom: '0.5rem',
        background: 'linear-gradient(to right, #00c6ff, #0072ff)',
        WebkitBackgroundClip: 'text',
        WebkitTextFillColor: 'transparent'
      }}>
        QuizLive
      </h1>
      <p style={{ marginBottom: '1.5rem', color: '#555' }}>
        Sign in to continue
      </p>
      <button
        onClick={handleGoogle}
        style={{
          padding: '12px 18px',
          borderRadius: 8,
          border: 'none',
          background: '#4285F4',
          color: 'white',
          fontWeight: 500,
          fontSize: 14,
          cursor: 'pointer',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
        }}
      >
        Continue with Google
      </button>
      <p style={{ marginTop: 24, fontSize: 13, color: '#555' }}>
        You only need to log in if you want to <strong>create or host quizzes</strong>.
        <br />
        To <strong>join a quiz</strong>, ask the host for a participant link — no login required.
      </p>
    </div>
  );
}