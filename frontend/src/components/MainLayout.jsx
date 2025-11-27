import React from 'react';
import AnimatedBackground from './AnimatedBackground';
import Navbar from './Navbar';

const MainLayout = ({
  children,
  header,               // 커스텀 헤더 (없으면 기본 Navbar 사용)
  enableAnimation = true, // 배경 애니메이션 사용 여부
  backgroundColor = '#ffffff', // 기본 배경색
  align = 'center'      // 컨텐츠 정렬: 'center' | 'top'
}) => {
  return (
      <div style={{...styles.container, backgroundColor}}>

        {/* 1. 배경 애니메이션 (옵션) */}
        {enableAnimation && <AnimatedBackground />}

        {/* 2. 헤더 (props로 전달받거나, 없으면 기본 Navbar) */}
        {header === undefined ? <Navbar /> : header}

        {/* 3. 메인 컨텐츠 */}
        <main style={{
          ...styles.mainContent,
          // 'top'일 경우 위쪽 정렬, 'center'면 중앙 정렬
          justifyContent: align === 'center' ? 'center' : 'flex-start',
          alignItems: align === 'center' ? 'center' : 'stretch',
          paddingTop: align === 'top' ? '40px' : '0', // top 정렬일 때 상단 여백
        }}>
          {children}
        </main>

        <footer style={styles.footer}>
          © 2025 Modern Todo List. All rights reserved.
        </footer>
      </div>
  );
};

const styles = {
  container: {
    minHeight: '100vh',
    width: '100vw',
    position: 'relative',
    overflowX: 'hidden', // 가로 스크롤 방지
    display: 'flex',
    flexDirection: 'column',
    fontFamily: "'Pretendard', sans-serif",
  },
  mainContent: {
    flex: 1,
    position: 'relative',
    zIndex: 1,
    display: 'flex',
    flexDirection: 'column',
    padding: '0 20px',
    width: '100%',
    maxWidth: '1200px', // 전체 컨텐츠 최대 폭 제한
    margin: '0 auto',   // 중앙 정렬
    boxSizing: 'border-box',
  },
  footer: {
    position: 'relative',
    zIndex: 1,
    textAlign: 'center',
    padding: '20px 0',
    fontSize: '13px',
    color: '#94a3b8', // TodoPage 회색 배경에서도 잘 보이게 조정
  }
};

export default MainLayout;