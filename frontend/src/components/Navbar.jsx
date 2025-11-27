import React from 'react';
import { useNavigate } from 'react-router-dom';

const Navbar = () => {
  const navigate = useNavigate();

  return (
      <nav style={styles.nav}>
        <div style={styles.logo} onClick={() => navigate('/')}>TodoApp</div>
        <div style={styles.navLinks}>
          <button style={styles.ghostButton} onClick={() => navigate('/login')}>로그인</button>
          <button style={styles.navButton} onClick={() => navigate('/signup')}>회원가입</button>
        </div>
      </nav>
  );
};

const styles = {
  nav: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '0 5%',
    height: '80px',
    width: '100%',
    position: 'absolute', // 배경 위에 뜨도록
    top: 0, left: 0,
    zIndex: 10,
    boxSizing: 'border-box', // padding 포함 계산
  },
  logo: {
    fontWeight: '800',
    fontSize: '26px',
    color: '#15803d',
    cursor: 'pointer',
    letterSpacing: '-1px',
  },
  navLinks: { display: 'flex', gap: '10px' },
  navButton: {
    padding: '10px 24px',
    backgroundColor: '#15803d',
    color: 'white',
    border: 'none',
    borderRadius: '25px',
    cursor: 'pointer',
    fontWeight: '600',
    fontSize: '14px',
  },
  ghostButton: {
    padding: '10px 24px',
    backgroundColor: 'transparent',
    color: '#15803d',
    border: 'none',
    cursor: 'pointer',
    fontWeight: '600',
    fontSize: '14px',
  },
};

export default Navbar;