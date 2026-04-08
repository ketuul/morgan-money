import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPortfolio } from '../api/api';

export default function Dashboard() {
  const [portfolio, setPortfolio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const accountId = localStorage.getItem('accountId');
  const userName = localStorage.getItem('userName');

  useEffect(() => {
    loadPortfolio();
  }, []);

  const loadPortfolio = async () => {
    try {
      const res = await getPortfolio(accountId);
      setPortfolio(res.data);
    } catch (err) {
      setError('Failed to load portfolio');
    }
    setLoading(false);
  };

  const logout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const totalPL = portfolio?.holdings?.reduce((sum, h) => sum + h.profitLoss, 0) || 0;
  const totalValue = (portfolio?.cashBalance || 0) +
    (portfolio?.holdings?.reduce((sum, h) => sum + (h.currentPrice * h.quantity), 0) || 0);

  if (loading) return <div style={styles.loading}>Loading portfolio...</div>;

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <div>
          <h1 style={styles.logo}>Morgan Money</h1>
          <p style={styles.welcome}>Welcome back, {userName}</p>
        </div>
        <div style={styles.headerRight}>
          <button style={styles.tradeBtn} onClick={() => navigate('/trade')}>
            Trade
          </button>
          <button style={styles.logoutBtn} onClick={logout}>
            Logout
          </button>
        </div>
      </div>

      <div style={styles.statsRow}>
        <div style={styles.statCard}>
          <p style={styles.statLabel}>Total Portfolio Value</p>
          <p style={styles.statValue}>{portfolio?.currency} {totalValue.toFixed(2)}</p>
        </div>
        <div style={styles.statCard}>
          <p style={styles.statLabel}>Cash Balance</p>
          <p style={styles.statValue}>{portfolio?.currency} {portfolio?.cashBalance?.toFixed(2)}</p>
        </div>
        <div style={styles.statCard}>
          <p style={styles.statLabel}>Total P&L</p>
          <p style={{ ...styles.statValue, color: totalPL >= 0 ? '#10b981' : '#ef4444' }}>
            {totalPL >= 0 ? '+' : ''}{totalPL.toFixed(2)}
          </p>
        </div>
      </div>

      <div style={styles.section}>
        <h2 style={styles.sectionTitle}>Holdings</h2>
        {portfolio?.holdings?.length === 0 ? (
          <div style={styles.emptyState}>
            <p style={styles.emptyText}>No holdings yet.</p>
            <button style={styles.tradeBtn} onClick={() => navigate('/trade')}>
              Make your first trade
            </button>
          </div>
        ) : (
          <table style={styles.table}>
            <thead>
              <tr>
                {['Ticker', 'Quantity', 'Avg Buy Price', 'Current Price', 'P&L', 'P&L %'].map(h => (
                  <th key={h} style={styles.th}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {portfolio?.holdings?.map(holding => (
                <tr key={holding.id} style={styles.tr}>
                  <td style={styles.tdBold}>{holding.ticker}</td>
                  <td style={styles.td}>{holding.quantity}</td>
                  <td style={styles.td}>{holding.averageBuyPrice.toFixed(2)}</td>
                  <td style={styles.td}>{holding.currentPrice.toFixed(2)}</td>
                  <td style={{ ...styles.td, color: holding.profitLoss >= 0 ? '#10b981' : '#ef4444' }}>
                    {holding.profitLoss >= 0 ? '+' : ''}{holding.profitLoss.toFixed(2)}
                  </td>
                  <td style={{ ...styles.td, color: holding.profitLossPercent >= 0 ? '#10b981' : '#ef4444' }}>
                    {holding.profitLossPercent >= 0 ? '+' : ''}{holding.profitLossPercent.toFixed(2)}%
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {error && <p style={styles.error}>{error}</p>}

      <button style={styles.refreshBtn} onClick={loadPortfolio}>
        Refresh Portfolio
      </button>
    </div>
  );
}

const styles = {
  container: { minHeight: '100vh', background: '#0a0e1a', padding: '24px 32px', color: '#fff' },
  loading: { color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh', background: '#0a0e1a', fontSize: 18 },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 32 },
  logo: { color: '#fff', fontSize: 24, fontWeight: 700, margin: 0 },
  welcome: { color: '#6b7280', fontSize: 14, margin: '4px 0 0' },
  headerRight: { display: 'flex', gap: 12 },
  tradeBtn: { padding: '10px 24px', background: '#3b82f6', color: '#fff', border: 'none', borderRadius: 8, cursor: 'pointer', fontWeight: 600 },
  logoutBtn: { padding: '10px 24px', background: 'transparent', color: '#6b7280', border: '1px solid #2d3748', borderRadius: 8, cursor: 'pointer' },
  statsRow: { display: 'flex', gap: 16, marginBottom: 32 },
  statCard: { flex: 1, background: '#141824', borderRadius: 12, padding: '20px 24px', border: '1px solid #2d3748' },
  statLabel: { color: '#6b7280', fontSize: 13, margin: '0 0 8px' },
  statValue: { color: '#fff', fontSize: 24, fontWeight: 700, margin: 0 },
  section: { background: '#141824', borderRadius: 12, padding: 24, border: '1px solid #2d3748', marginBottom: 24 },
  sectionTitle: { color: '#fff', fontSize: 18, fontWeight: 600, margin: '0 0 20px' },
  table: { width: '100%', borderCollapse: 'collapse' },
  th: { color: '#6b7280', fontSize: 13, fontWeight: 500, textAlign: 'left', padding: '8px 12px', borderBottom: '1px solid #2d3748' },
  tr: { borderBottom: '1px solid #1e2433' },
  td: { color: '#d1d5db', fontSize: 14, padding: '14px 12px' },
  tdBold: { color: '#fff', fontSize: 14, fontWeight: 600, padding: '14px 12px' },
  emptyState: { textAlign: 'center', padding: '40px 0' },
  emptyText: { color: '#6b7280', marginBottom: 16 },
  refreshBtn: { padding: '10px 24px', background: 'transparent', color: '#6b7280', border: '1px solid #2d3748', borderRadius: 8, cursor: 'pointer' },
  error: { color: '#ef4444', fontSize: 14 },
};
