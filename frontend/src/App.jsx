import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

// 페이지 컴포넌트 import
// 경로는 실제 파일 위치에 맞게 수정해주세요.
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import TodoPage from './pages/TodoPage';

function App() {
  return (
      <BrowserRouter>
        <Routes>
          {/* 1. 메인 페이지: 할 일 목록 (로그인 성공 후 이동) */}
          <Route path="/" element={<TodoPage />} />

          {/* 2. 로그인 페이지 */}
          <Route path="/login" element={<LoginPage />} />

          {/* 3. 회원가입 페이지 */}
          <Route path="/signup" element={<SignupPage />} />
        </Routes>
      </BrowserRouter>
  );
}

export default App;