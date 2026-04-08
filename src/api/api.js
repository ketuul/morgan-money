import axios from 'axios';

// Points to your Spring Boot backend
const BASE_URL = 'http://localhost:8080';

// Create an axios instance with the base URL
const api = axios.create({ baseURL: BASE_URL });

// Automatically attach the token to every request
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const signup = (name, email, password) =>
  api.post('/api/auth/signup', { name, email, password });

export const login = (email, password) =>
  api.post('/api/auth/login', { email, password });

export const getPortfolio = (accountId) =>
  api.get(`/api/portfolio/${accountId}`);

export const buyStock = (accountId, ticker, quantity) =>
  api.get(`/api/buy/${accountId}/${ticker}/${quantity}`);

export const sellStock = (accountId, ticker, quantity) =>
  api.get(`/api/sell/${accountId}/${ticker}/${quantity}`);

export const getPrice = (ticker) =>
  api.get(`/api/price/${ticker}`);

export const createAccount = (name, location, startingBalance) =>
  api.post('/api/account', { name, location, startingBalance });
