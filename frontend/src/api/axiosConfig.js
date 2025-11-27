import axios from 'axios';

const api = axios.create({
  // Vite Proxy 설정을 탔으므로 도메인 없이 '/api'만 써도 8080으로 감
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
  // ★ 중요: 세션(JSESSIONID) 쿠키를 주고받기 위한 필수 설정
  withCredentials: true,
});

export default api;