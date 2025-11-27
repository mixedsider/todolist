import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const useSignup = () => {
  const navigate = useNavigate();

  // 상태 관리
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  // 에러 메시지
  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [confirmError, setConfirmError] = useState('');

  // 성공 상태
  const [isSuccess, setIsSuccess] = useState(false);

  // --- 검증 로직 ---
  const validateEmail = (value) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(value)) {
      setEmailError('올바른 이메일 형식이 아닙니다.');
      return false;
    }
    setEmailError('');
    return true;
  };

  const validatePassword = (value) => {
    const passwordRegex = /^(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
    if (!passwordRegex.test(value)) {
      setPasswordError('비밀번호는 8자 이상, 특수문자 포함 필수입니다.');
      return false;
    }
    setPasswordError('');
    return true;
  };

  const validateConfirmPassword = (pass, confirm) => {
    if (pass !== confirm) {
      setConfirmError('비밀번호가 일치하지 않습니다.');
      return false;
    }
    setConfirmError('');
    return true;
  };

  // --- 이벤트 핸들러 ---
  const handleEmailChange = (e) => {
    const val = e.target.value;
    setEmail(val);
    if (val) validateEmail(val);
  };

  const handlePasswordChange = (e) => {
    const val = e.target.value;
    setPassword(val);
    if (val) {
      validatePassword(val);
      if (confirmPassword) validateConfirmPassword(val, confirmPassword);
    }
  };

  const handleConfirmChange = (e) => {
    const val = e.target.value;
    setConfirmPassword(val);
    if (val) validateConfirmPassword(password, val);
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    if (!validateEmail(email) || !validatePassword(password) || !validateConfirmPassword(password, confirmPassword)) return;

    try {
      const response = await api.post('/v1/members/signup', { email, password });
      if (response.status === 200) setIsSuccess(true);
    } catch (error) {
      if (error.response && error.response.status === 400) {
        alert('이미 사용 중인 이메일입니다.');
      } else {
        alert('회원가입 실패');
      }
    }
  };

  const handleLoginMove = () => {
    navigate('/login');
  };

  return {
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
  };
};

export default useSignup;