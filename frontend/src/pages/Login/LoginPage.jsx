import React from 'react';
import MainLayout from '../../components/MainLayout.jsx';
import useLogin from './useLogin.js';       // 분리한 JS 로직 import
import styles from './LoginPage.module.css'; // 분리한 CSS 스타일 import

const LoginPage = () => {
  // 로직 파일(Hook)에서 필요한 상태와 함수만 가져옴
  const {
    email,
    setEmail,
    password,
    setPassword,
    isLoading,
    handleLogin,
    handleSignupMove
  } = useLogin();

  // 1. 로딩 화면
  if (isLoading) {
    return (
        <MainLayout>
          <div className={styles.loadingContainer}>
            로그인 정보를 확인하고 있습니다...
          </div>
        </MainLayout>
    );
  }

  // 2. 메인 로그인 화면 (HTML 구조에 집중)
  return (
      <MainLayout>
        <div className={styles.card}>
          <div className={styles.header}>
            <h2 className={styles.title}>다시 만나서 반가워요! 👋</h2>
            <p className={styles.subtitle}>
              오늘 할 일을 체크하고,<br />
              생산적인 하루를 시작해보세요.
            </p>
          </div>

          <form onSubmit={handleLogin} className={styles.form}>
            <div className={styles.inputGroup}>
              <label className={styles.label}>이메일</label>
              <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="user@example.com"
                  className={styles.input}
                  required
              />
            </div>

            <div className={styles.inputGroup}>
              <label className={styles.label}>비밀번호</label>
              <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="비밀번호 입력"
                  className={styles.input}
                  required
              />
            </div>

            <button type="submit" className={styles.loginButton}>
              로그인
            </button>
          </form>

          <div className={styles.divider}>
          </div>

          <div className={styles.footer}>
            <span className={styles.footerText}>아직 계정이 없으신가요?</span>
            <button onClick={handleSignupMove} className={styles.signupLink}>
              회원가입 하기
            </button>
          </div>
        </div>
      </MainLayout>
  );
};

export default LoginPage;