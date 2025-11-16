const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID as string;

export default function Login() {
  const handleGoogle = () => {
    const redirectUri = '${window.location.origin}/oauth2/callback/google';
    const params = new URLSearchParams({
      client_id: GOOGLE_CLIENT_ID,
      redirect_uri: redirectUri,
      response_type: 'code',
      scope: 'openid email profile',
      prompt: 'consent',
      access_type: 'online',
    });
    const url = 'https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}';
    window.location.href = url;
  };

  return (
    <div style={{ maxWidth: 420, margin: '80px auto', padding: 16, border: '1px solid #eee', borderRadius: 8 }}>
      <h1>Live Quiz</h1>
      <p>Sign in to continue.</p>
      <button onClick={handleGoogle} style={{ padding: '10px 14px', borderRadius: 6, border: '1px solid #999', cursor: 'pointer' }}>
        Continue with Google
      </button>
      <p style={{ marginTop: 16, fontSize: 12, color: '#666' }}>
        Redirect URI:
        <br />
        <code>{'${window.location.origin}/oauth2/callback/google'}</code>
      </p>
    </div>
  );
}