import React from 'react';
import MainLayout from '../../components/MainLayout'; // 상위 폴더로 이동하여 import
import useHome from './useHome';       // 같은 폴더 로직
import styles from './HomePage.module.css'; // 같은 폴더 스타일

const HomePage = () => {
  // 로직 분리: 이동 함수만 받아옴
  const { handleStartClick, handleSignupClick } = useHome();

  return (
      <MainLayout>
        <div className={styles.contentWrapper}>
          <div className={styles.badge}>✨ Simple & Powerful</div>

          <h1 className={styles.title}>
            현대적인 Todo <br />
            <span className={styles.highlight}>Todo List</span>
          </h1>

          <p className={styles.subtitle}>
            복잡한 일상은 정리하고, 중요한 일에 집중하세요.<br />
            당신의 하루를 완벽하게 관리해 드립니다.
          </p>

          <div className={styles.buttonGroup}>
            <button className={styles.primaryButton} onClick={handleStartClick}>
              시작하기
            </button>
            <button className={styles.secondaryButton} onClick={handleSignupClick}>
              계정 만들기
            </button>
          </div>
        </div>
      </MainLayout>
  );
};

export default HomePage;