import { useNavigate } from 'react-router-dom';

const useHome = () => {
  const navigate = useNavigate();

  const handleStartClick = () => {
    navigate('/login');
  };

  const handleSignupClick = () => {
    navigate('/signup');
  };

  return {
    handleStartClick,
    handleSignupClick
  };
};

export default useHome;