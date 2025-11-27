import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig'; // 아까 만든 axios 설정 사용

const SignupPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const navigate = useNavigate();

  const handleSignup = async (e) => {
    e.preventDefault();

    // 간단한 클라이언트 유효성 검사 (선택 사항)
    if (password.length < 8) {
      alert('비밀번호는 8자 이상이어야 합니다.');
      return;
    }

    try {
      // API 명세: POST /api/v1/members/signup
      const response = await api.post('/v1/members/signup', {
        email: email,
        password: password
      });

      if (response.status === 200) {
        alert('회원가입 성공! 로그인 페이지로 이동합니다.');
        navigate('/login'); // 성공 시 로그인 페이지로 이동
      }
    } catch (error) {
      console.error('회원가입 에러:', error);

      // API 문서에 따른 에러 처리
      if (error.response) {
        if (error.response.status === 400) {
          alert('입력 값이 올바르지 않습니다. (이메일 형식, 비밀번호 규칙 확인)');
        } else if (error.response.status === 500) {
          alert('이미 가입된 이메일이거나 서버 오류입니다.');
        } else {
          alert('회원가입 실패: ' + error.response.status);
        }
      } else {
        alert('서버와 연결할 수 없습니다.');
      }
    }
  };

  return (
      <div style={{ padding: '20px' }}>
        <h2>회원가입</h2>
        <form onSubmit={handleSignup}>
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
                placeholder="8자 이상, 특수문자 포함"
                required
            />
          </div>
          <button type="submit" style={{ marginTop: '20px' }}>
            가입하기
          </button>
        </form>
      </div>
  );
};

export default SignupPage;