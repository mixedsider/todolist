import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

// 페이지 컴포넌트 import
// 경로는 실제 파일 위치에 맞게 수정해주세요.
import LoginPage from './pages/Login/LoginPage.jsx';
import SignupPage from './pages/Signup/SignupPage';
import TodoPage from './pages/Todo/TodoPage';
import HomePage from './pages/Home/HomePage';

function App() {
  return (
      <BrowserRouter>
        <Routes>
          {/* 메인 홈페이지 (랜딩 페이지) */}
          <Route path="/" element={<HomePage />} />

          {/* 기능 페이지: 할 일 목록 (로그인 성공 후 이동) */}
          <Route path="/todo" element={<TodoPage />} />

          {/* 로그인 / 회원가입 */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
        </Routes>
      </BrowserRouter>
  );
}

export default App;