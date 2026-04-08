import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { buyStock, sellStock, getPrice } from '../api/api';

export default function Trade() {
  const [ticker, setTicker] = useState('');
  const [quantity, setQuantity] = useState('');
  const [livePrice, setLivePrice] = useState(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const accountId = localStorage.getItem('accountId');

  const checkPrice = async () => {
    if (!ticker) return;
    setLoading(true);
    setError('');
    try {
      const res = await getPrice(ticker.toUpperCase());
      setLivePrice(res.data);
    } catch (err) {
      setError('Could not fetch price. Check the ticker symbol.');
    }
    setLoading(false);
  };

  const handleBuy = async () => {
    if (!ticker || !quantity) return;
    setLoading(true);
    setMessage('');
    setError('');
    try {
      const res = await buyStock(accountId, ticker.toUpperCase(), quantity);
      setMessage(res.data);
    } catch (err) {
      setError('Trade failed. Please try again.');
    }
    setLoading(false);
  };

  const handleSell = async () => {
    if (!ticker || !quantity) return;
    setLoading(true);
    setMessage('');
    setError('');
    try {
      const res = await sellStock(accountId, ticker.toUpperCase(), quantity);
      setMessage(res.data);
    } catch (err) {
      setError('Trade failed. Please try again.');
    }
    setLoading(false);
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h1 style={styles.logo}>Morgan Money</h1>
        <button style={styles.backBtn} onClick={() => navigate('/dashboard')}>
          Back to Portfolio
        </button>
      </div>

      <div style={styles.card}>
        <h2 style={styles.title}>Place a Trade</h2>

        <label style={styles.label}>Ticker Symbol</label>
        <div style={styles.row}>
          <input
            style={{ ...styles.input, flex: 1 }}
            placeholder="e.g. AAPL, TSLA, GOOGL"
            value={ticker}
            onChange={e => setTicker(e.target.value.toUpperCase())}
          />
          <button style={styles.priceBtn} onClick={checkPrice} disabled={loading}>
            Check Price
          </button>
        </div>

        {livePrice && (
          <div style={styles.priceBox}>
            <p style={styles.priceText}>{livePrice}</p>
          </div>
        )}

        <label style={styles.label}>Quantity</label>
        <input
          style={styles.input}
          placeholder="Number of shares"
          type="number"
          value={quantity}
          onChange={e => setQuantity(e.target.value)}
        />

        <div style={styles.buttonRow}>
          <button style={styles.buyBtn} onClick={handleBuy} disabled={loading}>
            {loading ? 'Processing...' : 'Buy'}
          </button>
          <button style={styles.sellBtn} onClick={handleSell} disabled={loading}>
            {loading ? 'Processing...' : 'Sell'}
          </button>
        </div>

        {message && <div style={styles.success}>{message}</div>}
        {error && <div style={styles.error}>{error}</div>}
      </div>
    </div>
  );
}

const styles = {
  container: { minHeight: '100vh', background: '#0a0e1a', padding: '24px 32px', color: '#fff' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 32 },
  logo: { color: '#fff', fontSize: 24, fontWeight: 700, margin: 0 },
  backBtn: { padding: '10px 24px', background: 'transparent', color: '#6b7280', border: '1px solid #2d3748', borderRadius: 8, cursor: 'pointer' },
  card: { maxWidth: 480, background: '#141824', borderRadius: 16, padding: 32, border: '1px solid #2d3748' },
  title: { color: '#fff', fontSize: 20, fontWeight: 600, margin: '0 0 24px' },
  label: { color: '#6b7280', fontSize: 13, display: 'block', marginBottom: 8 },
  row: { display: 'flex', gap: 8, marginBottom: 20 },
  input: { width: '100%', padding: '12px 16px', background: '#1e2433', border: '1px solid #2d3748', borderRadius: 8, color: '#fff', fontSize: 14, marginBottom: 20, boxSizing: 'border-box' },
  priceBtn: { padding: '12px 16px', background: '#1e2433', color: '#6b7280', border: '1px solid #2d3748', borderRadius: 8, cursor: 'pointer', whiteSpace: 'nowrap' },
  priceBox: { background: '#1e2433', borderRadius: 8, padding: '12px 16px', marginBottom: 20, border: '1px solid #2d3748' },
  priceText: { color: '#10b981', fontSize: 15, fontWeight: 600, margin: 0 },
  buttonRow: { display: 'flex', gap: 12, marginTop: 4 },
  buyBtn: { flex: 1, padding: '14px 0', background: '#10b981', color: '#fff', border: 'none', borderRadius: 8, fontSize: 16, fontWeight: 600, cursor: 'pointer' },
  sellBtn: { flex: 1, padding: '14px 0', background: '#ef4444', color: '#fff', border: 'none', borderRadius: 8, fontSize: 16, fontWeight: 600, cursor: 'pointer' },
  success: { marginTop: 16, padding: '12px 16px', background: '#064e3b', borderRadius: 8, color: '#10b981', fontSize: 14 },
  error: { marginTop: 16, padding: '12px 16px', background: '#450a0a', borderRadius: 8, color: '#ef4444', fontSize: 14 },
};
