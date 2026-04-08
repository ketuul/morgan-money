import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Trade from './pages/Trade';

// Check if the user is logged in by looking for a token in localStorage
const isLoggedIn = () => !!localStorage.getItem('token');

// ProtectedRoute — if not logged in, redirect to login page
const ProtectedRoute = ({ children }) => {
  return isLoggedIn() ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={
          <ProtectedRoute><Dashboard /></ProtectedRoute>
        } />
        <Route path="/trade" element={
          <ProtectedRoute><Trade /></ProtectedRoute>
        } />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
