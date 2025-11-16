const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID as string;

export default function Login() {
  const handleGoogle = () => {
    const redirectUri = `${window.location.origin}/oauth2/callback/google/`;
    const params = new URLSearchParams({
      client_id: GOOGLE_CLIENT_ID,
      redirect_uri: redirectUri,
      response_type: 'code',
      scope: 'openid email profile',
      prompt: 'consent',
      access_type: 'online'
    });
    const url = `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`;
    window.location.href = url;
  };

  return (
    <div style={{
      maxWidth: 420,
      margin: '80px auto',
      padding: '2rem',
      borderRadius: '16px',
      background: 'linear-gradient(to right, #fdfbfb, #ebedee)',
      boxShadow: '0 8px 24px rgba(0,0,0,0.1)',
      fontFamily: 'Segoe UI, sans-serif',
      textAlign: 'center',
      color: '#333'
    }}>
      <h1 style={{
        fontSize: '2rem',
        marginBottom: '1rem',
        background: 'linear-gradient(to right, #00c6ff, #0072ff)',
        WebkitBackgroundClip: 'text',
        WebkitTextFillColor: 'transparent'
      }}>
        Live Quiz
      </h1>
      <p style={{ fontSize: '1rem', marginBottom: '2rem' }}>Sign in to continue and join the fun!</p>

      <button
        onClick={handleGoogle}
        style={{
          backgroundColor: '#4285F4',
          color: '#fff',
          border: 'none',
          padding: '0.75rem 1.25rem',
          borderRadius: '8px',
          fontSize: '1rem',
          fontWeight: 600,
          cursor: 'pointer',
          transition: 'background 0.3s ease',
          boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
        }}
        onMouseOver={(e) => (e.currentTarget.style.backgroundColor = '#3367D6')}
        onMouseOut={(e) => (e.currentTarget.style.backgroundColor = '#4285F4')}
      >
        Continue with Google
      </button>

      <p style={{
        marginTop: '2rem',
        fontSize: '0.85rem',
        color: '#666',
        lineHeight: 1.5,
        backgroundColor: '#fff',
        padding: '1rem',
        borderRadius: '8px',
        boxShadow: '0 2px 6px rgba(0,0,0,0.05)'
      }}>
        Participants do not need to log in, ask your teacher for the quiz link!
        <br />
        <code style={{
          display: 'inline-block',
          marginTop: '0.5rem',
          backgroundColor: '#f1f1f1',
          padding: '0.25rem 0.5rem',
          borderRadius: '4px',
          fontSize: '0.75rem'
        }}>
        </code>
      </p>
    </div>
  );
}