import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig.js';

const useLogin = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  // 로그인 상태 체크 로직
  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        await api.get('/v1/todos', {
          headers: { 'Cache-Control': 'no-cache' }
        });
        navigate('/todo', { replace: true });
      } catch (error) {
        // 에러 처리 로직 (401 등)
      }
      setIsLoading(false);
    };

    checkLoginStatus();
  }, [navigate]);

  // 로그인 핸들러
  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/v1/members/login', {
        email: email,
        password: password,
      });

      if (response.status === 200) {
        navigate('/todo');
      }
    } catch (error) {
      if (error.response && error.response.status === 401) {
        alert('이메일 또는 비밀번호가 일치하지 않습니다.');
      } else {
        alert('로그인 중 오류가 발생했습니다.');
      }
    }
  };

  const handleSignupMove = () => {
    navigate('/signup');
  };

  // UI에서 필요한 데이터와 함수들을 반환
  return {
    email,
    setEmail,
    password,
    setPassword,
    isLoading,
    handleLogin,
    handleSignupMove
  };
};

export default useLogin;