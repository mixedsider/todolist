import React from 'react';
import MainLayout from '../../components/MainLayout';
import useSignup from './useSignup';
import styles from './SignupPage.module.css';

const SignupPage = () => {
  const {
    email,
    password,
    confirmPassword,
    emailError,
    passwordError,
    confirmError,
    isSuccess,
    handleEmailChange,
    handlePasswordChange,
    handleConfirmChange,
    handleSignup,
    handleLoginMove
  } = useSignup();

  return (
      <MainLayout>
        <div className={styles.card}>
          {isSuccess ? (
              /* 회원가입 성공 화면 */
              <div className={styles.successWrapper}>
                <div className={styles.checkIcon}>🎉</div>
                <h2 className={styles.successTitle}>회원가입 완료!</h2>
                <p className={styles.successText}>
                  환영합니다!<br />이제 로그인을 해주세요.
                </p>
                <button className={styles.primaryButton} onClick={handleLoginMove}>
                  로그인 하러 가기
                </button>
              </div>
          ) : (
              /* 회원가입 폼 화면 */
              <>
                <h2 className={styles.title}>계정 만들기</h2>
                <p className={styles.subtitle}>이메일로 간편하게 가입하세요.</p>

                <form onSubmit={handleSignup} className={styles.form}>
                  <div className={styles.inputGroup}>
                    <label className={styles.label}>이메일</label>
                    <input
                        type="email"
                        value={email}
                        onChange={handleEmailChange}
                        className={styles.input}
                        placeholder="user@example.com"
                    />
                    {emailError && <span className={styles.errorText}>{emailError}</span>}
                  </div>

                  <div className={styles.inputGroup}>
                    <label className={styles.label}>비밀번호</label>
                    <input
                        type="password"
                        value={password}
                        onChange={handlePasswordChange}
                        className={styles.input}
                        placeholder="8자 이상, 특수문자 포함"
                    />
                    {passwordError && <span className={styles.errorText}>{passwordError}</span>}
                  </div>

                  <div className={styles.inputGroup}>
                    <label className={styles.label}>비밀번호 확인</label>
                    <input
                        type="password"
                        value={confirmPassword}
                        onChange={handleConfirmChange}
                        className={styles.input}
                        placeholder="비밀번호 재입력"
                    />
                    {confirmError && <span className={styles.errorText}>{confirmError}</span>}
                  </div>

                  <button type="submit" className={styles.primaryButton}>
                    가입하기
                  </button>
                </form>
              </>
          )}
        </div>
      </MainLayout>
  );
};

export default SignupPage;