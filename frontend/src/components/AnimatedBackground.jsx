import React from 'react';

const AnimatedBackground = () => {
  return (
      <>
        <style>
          {`
          @keyframes breathe {
            0% { transform: scale(1); opacity: 0.7; }
            50% { transform: scale(1.5); opacity: 1; }
            100% { transform: scale(1); opacity: 0.7; }
          }
        `}
        </style>
        <div style={styles.background}></div>
      </>
  );
};

const styles = {
  background: {
    position: 'absolute',
    top: 0, left: 0, width: '100%', height: '100%',
    background: 'radial-gradient(circle at center, #86efac 0%, #dcfce7 40%, #ffffff 80%)',
    animation: 'breathe 6s ease-in-out infinite',
    zIndex: 0,
    transformOrigin: 'center center',
  },
};

export default AnimatedBackground;