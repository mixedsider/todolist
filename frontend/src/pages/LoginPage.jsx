import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig'; // 위에서 만든 axios 인스턴스 import

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault(); // form submit 시 새로고침 방지

    try {
      // API 명세: POST /api/v1/members/login
      const response = await api.post('/v1/members/login', {
        email: email,
        password: password,
      });

      if (response.status === 200) {
        console.log('로그인 성공:', response.data);
        // 로그인 성공 시 메인 페이지로 이동
        navigate('/');
      }
    } catch (error) {
      console.error('로그인 에러:', error);
      // 에러 처리 (401 등)
      if (error.response && error.response.status === 401) {
        alert('아이디 또는 비밀번호가 일치하지 않습니다.');
      } else {
        alert('로그인 중 오류가 발생했습니다.');
      }
    }
  };

  return (
      <div style={{ padding: '20px' }}>
        <h2>로그인</h2>
        <form onSubmit={handleLogin}>
          <div>
            <label>이메일: </label>
            <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="user@example.com"
                required
            />
          </div>
          <div style={{ marginTop: '10px' }}>
            <label>비밀번호: </label>
            <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="비밀번호 입력"
                required
            />
          </div>
          <button type="submit" style={{ marginTop: '20px' }}>
            로그인
          </button>
        </form>
        <div style={{ marginTop: '20px' }}>
          <button onClick={() => navigate('/signup')}>
            회원가입 하러 가기
          </button>
        </div>
      </div>
  );
};

export default LoginPage;