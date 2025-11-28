import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import SignupPage from './SignupPage';
import api from '../../api/axiosConfig';

// 1. Mocking: Router (페이지 이동 확인용)
const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  ...vi.importActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

// 2. Mocking: Axios (API 요청 가로채기)
vi.mock('../../api/axiosConfig');

// 3. Mocking: Layout (테스트 단순화)
vi.mock('../../components/MainLayout', () => ({
  default: ({ children }) => <div>{children}</div>,
}));

describe('SignupPage 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // window.alert Mocking (에러 메시지 확인용)
    vi.spyOn(window, 'alert').mockImplementation(() => {});
  });

  it('회원가입 폼 요소들이 정상적으로 렌더링되어야 한다', () => {
    render(<SignupPage />);

    expect(screen.getByText('계정 만들기')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('user@example.com')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('8자 이상, 특수문자 포함')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('비밀번호 재입력')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '가입하기' })).toBeInTheDocument();
  });

  it('유효하지 않은 이메일 입력 시 에러 메시지가 나와야 한다', async () => {
    render(<SignupPage />);
    const emailInput = screen.getByPlaceholderText('user@example.com');

    // 잘못된 이메일 입력
    fireEvent.change(emailInput, { target: { value: 'wrong-email' } });

    // 에러 메시지 확인
    await waitFor(() => {
      expect(screen.getByText('올바른 이메일 형식이 아닙니다.')).toBeInTheDocument();
    });
  });

  it('비밀번호가 서로 일치하지 않으면 에러 메시지가 나와야 한다', async () => {
    render(<SignupPage />);

    // 비밀번호 입력
    const pwInput = screen.getByPlaceholderText('8자 이상, 특수문자 포함');
    fireEvent.change(pwInput, { target: { value: 'password!123' } });

    // 확인 비밀번호 다르게 입력
    const confirmInput = screen.getByPlaceholderText('비밀번호 재입력');
    fireEvent.change(confirmInput, { target: { value: 'different!123' } });

    await waitFor(() => {
      expect(screen.getByText('비밀번호가 일치하지 않습니다.')).toBeInTheDocument();
    });
  });

  it('회원가입 성공 시 축하 메시지가 뜨고 로그인 버튼이 보여야 한다', async () => {
    // API 성공 응답 Mock
    api.post.mockResolvedValue({ status: 200 });

    render(<SignupPage />);

    // 유효한 정보 입력
    fireEvent.change(screen.getByPlaceholderText('user@example.com'), { target: { value: 'valid@test.com' } });
    fireEvent.change(screen.getByPlaceholderText('8자 이상, 특수문자 포함'), { target: { value: 'password!123' } });
    fireEvent.change(screen.getByPlaceholderText('비밀번호 재입력'), { target: { value: 'password!123' } });

    // 가입하기 버튼 클릭
    const submitButton = screen.getByRole('button', { name: '가입하기' });
    fireEvent.click(submitButton);

    // API 호출 검증
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/v1/members/signup', {
        email: 'valid@test.com',
        password: 'password!123'
      });
    });

    // 화면 전환 확인 (폼이 사라지고 성공 메시지가 떠야 함)
    expect(screen.getByText('회원가입 완료!')).toBeInTheDocument();

    // '로그인 하러 가기' 버튼이 생겼는지 확인
    const loginLinkBtn = screen.getByRole('button', { name: '로그인 하러 가기' });
    expect(loginLinkBtn).toBeInTheDocument();

    // 버튼 클릭 시 이동 확인
    fireEvent.click(loginLinkBtn);
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  it('이미 가입된 이메일(400 에러)인 경우 alert가 떠야 한다', async () => {
    // API 400 에러 Mock
    api.post.mockRejectedValue({ response: { status: 400 } });

    render(<SignupPage />);

    // 유효한 정보 입력 (이미 있는 이메일 가정)
    fireEvent.change(screen.getByPlaceholderText('user@example.com'), { target: { value: 'exist@test.com' } });
    fireEvent.change(screen.getByPlaceholderText('8자 이상, 특수문자 포함'), { target: { value: 'password!123' } });
    fireEvent.change(screen.getByPlaceholderText('비밀번호 재입력'), { target: { value: 'password!123' } });

    fireEvent.click(screen.getByRole('button', { name: '가입하기' }));

    // alert 호출 확인
    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('이미 사용 중인 이메일입니다.');
    });
  });
});