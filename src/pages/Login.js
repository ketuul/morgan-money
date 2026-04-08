import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login, signup, createAccount } from '../api/api';

export default function Login() {
  const [isSignup, setIsSignup] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [location, setLocation] = useState('USA');
  const [balance, setBalance] = useState('10000');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async () => {
    setError('');
    setLoading(true);
    try {
      if (isSignup) {
        // Sign up the user
        const res = await signup(name, email, password);
        const token = res.data.token;
        const userId = res.data.userId;
        localStorage.setItem('token', token);

        // Create their trading account
        const accountRes = await createAccount(name, location, parseFloat(balance));
        const accountId = accountRes.data.split('Your account ID is: ')[1];
        localStorage.setItem('accountId', accountId);
        localStorage.setItem('userName', name);
      } else {
        // Log in
        const res = await login(email, password);
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('accountId', res.data.userId);
        localStorage.setItem('userName', res.data.name || email);
      }
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.error || 'Something went wrong');
    }
    setLoading(false);
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.title}>Morgan Money</h1>
        <p style={styles.subtitle}>Global Trading Platform</p>

        <div style={styles.tabs}>
          <button
            style={isSignup ? styles.tabInactive : styles.tabActive}
            onClick={() => setIsSignup(false)}>
            Login
          </button>
          <button
            style={isSignup ? styles.tabActive : styles.tabInactive}
            onClick={() => setIsSignup(true)}>
            Sign Up
          </button>
        </div>

        {isSignup && (
          <input style={styles.input} placeholder="Full name"
            value={name} onChange={e => setName(e.target.value)} />
        )}

        <input style={styles.input} placeholder="Email"
          value={email} onChange={e => setEmail(e.target.value)} />

        <input style={styles.input} placeholder="Password"
          type="password" value={password}
          onChange={e => setPassword(e.target.value)} />

        {isSignup && (
          <>
            <select style={styles.input} value={location}
              onChange={e => setLocation(e.target.value)}>
              <option value="USA">USA (USD)</option>
              <option value="UK">UK (GBP)</option>
              <option value="Europe">Europe (EUR)</option>
              <option value="Japan">Japan (JPY)</option>
              <option value="India">India (INR)</option>
              <option value="Canada">Canada (CAD)</option>
              <option value="Australia">Australia (AUD)</option>
              <option value="China">China (CNY)</option>
            </select>
            <input style={styles.input} placeholder="Starting balance"
              type="number" value={balance}
              onChange={e => setBalance(e.target.value)} />
          </>
        )}

        {error && <p style={styles.error}>{error}</p>}

        <button style={styles.button} onClick={handleSubmit} disabled={loading}>
          {loading ? 'Please wait...' : isSignup ? 'Create Account' : 'Login'}
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '100vh',
    background: '#0a0e1a',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  card: {
    background: '#141824',
    borderRadius: 16,
    padding: 40,
    width: 380,
    boxShadow: '0 8px 32px rgba(0,0,0,0.4)',
  },
  title: {
    color: '#ffffff',
    fontSize: 28,
    fontWeight: 700,
    textAlign: 'center',
    margin: 0,
  },
  subtitle: {
    color: '#6b7280',
    textAlign: 'center',
    marginBottom: 32,
    fontSize: 14,
  },
  tabs: {
    display: 'flex',
    marginBottom: 24,
    borderRadius: 8,
    overflow: 'hidden',
    border: '1px solid #2d3748',
  },
  tabActive: {
    flex: 1,
    padding: '10px 0',
    background: '#3b82f6',
    color: '#fff',
    border: 'none',
    cursor: 'pointer',
    fontWeight: 600,
    fontSize: 14,
  },
  tabInactive: {
    flex: 1,
    padding: '10px 0',
    background: 'transparent',
    color: '#6b7280',
    border: 'none',
    cursor: 'pointer',
    fontSize: 14,
  },
  input: {
    width: '100%',
    padding: '12px 16px',
    background: '#1e2433',
    border: '1px solid #2d3748',
    borderRadius: 8,
    color: '#ffffff',
    fontSize: 14,
    marginBottom: 12,
    boxSizing: 'border-box',
  },
  button: {
    width: '100%',
    padding: '14px 0',
    background: '#3b82f6',
    color: '#fff',
    border: 'none',
    borderRadius: 8,
    fontSize: 16,
    fontWeight: 600,
    cursor: 'pointer',
    marginTop: 8,
  },
  error: {
    color: '#ef4444',
    fontSize: 14,
    marginBottom: 12,
  },
};
