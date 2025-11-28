import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import LoginPage from './LoginPage';
import api from '../../api/axiosConfig'; // Axios 인스턴스

// 1. React Router Mocking (페이지 이동 감지용)
const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  ...vi.importActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

// 2. Axios API Mocking (서버 요청 가로채기)
vi.mock('../../api/axiosConfig');

// 3. MainLayout Mocking (배경 애니메이션 등 불필요한 렌더링 방지)
vi.mock('../../components/MainLayout', () => ({
  default: ({ children }) => <div>{children}</div>,
}));

describe('LoginPage 테스트', () => {

  beforeEach(() => {
    // 각 테스트 실행 전 Mock 초기화
    vi.clearAllMocks();

    // 페이지 진입 시 자동 실행되는 "세션 체크(GET /v1/todos)"를 실패하도록 설정
    // (성공하면 바로 페이지가 이동해버리므로, 로그인 폼 테스트를 위해 실패 처리)
    api.get.mockRejectedValue({ response: { status: 401 } });
  });

  it('로그인 폼이 정상적으로 렌더링되어야 한다', async () => {
    render(<LoginPage />);

    // 로딩이 끝나고 폼이 보일 때까지 기다림
    await waitFor(() => {
      expect(screen.getByText('다시 만나서 반가워요! 👋')).toBeInTheDocument();
    });

    // 이메일, 비밀번호 입력창과 버튼 확인
    expect(screen.getByPlaceholderText('user@example.com')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('비밀번호 입력')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '로그인' })).toBeInTheDocument();
  });

  it('사용자가 입력을 하면 값이 변경되어야 한다', async () => {
    render(<LoginPage />);
    await waitFor(() => screen.getByPlaceholderText('user@example.com'));

    const emailInput = screen.getByPlaceholderText('user@example.com');
    const passwordInput = screen.getByPlaceholderText('비밀번호 입력');

    // 타이핑 시뮬레이션
    fireEvent.change(emailInput, { target: { value: 'test@test.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    expect(emailInput.value).toBe('test@test.com');
    expect(passwordInput.value).toBe('password123');
  });

  it('로그인 성공 시 /todo 페이지로 이동해야 한다', async () => {
    // 로그인 API 성공 응답 설정 (Mock)
    api.post.mockResolvedValue({ status: 200, data: { message: 'success' } });

    render(<LoginPage />);
    await waitFor(() => screen.getByPlaceholderText('user@example.com'));

    // 입력 및 제출
    fireEvent.change(screen.getByPlaceholderText('user@example.com'), { target: { value: 'user@example.com' } });
    fireEvent.change(screen.getByPlaceholderText('비밀번호 입력'), { target: { value: 'pass123' } });

    const loginButton = screen.getByRole('button', { name: '로그인' });
    fireEvent.click(loginButton);

    // API가 호출되었는지 확인
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/v1/members/login', {
        email: 'user@example.com',
        password: 'pass123'
      });
    });

    // 페이지 이동이 발생했는지 확인
    expect(mockNavigate).toHaveBeenCalledWith('/todo');
  });

  it('로그인 실패 시(401) 경고창(alert)이 떠야 한다', async () => {
    // window.alert Mocking
    const alertMock = vi.spyOn(window, 'alert').mockImplementation(() => {});

    // 로그인 API 실패 응답 설정
    api.post.mockRejectedValue({ response: { status: 401 } });

    render(<LoginPage />);
    await waitFor(() => screen.getByPlaceholderText('user@example.com'));

    const emailInput = screen.getByPlaceholderText('user@example.com');
    const passwordInput = screen.getByPlaceholderText('비밀번호 입력');

    fireEvent.change(emailInput, { target: { value: 'wrong@user.com' } });
    fireEvent.change(passwordInput, { target: { value: 'wrongpass' } });

    const loginButton = screen.getByRole('button', { name: '로그인' });
    fireEvent.click(loginButton);

    await waitFor(() => {
      expect(alertMock).toHaveBeenCalledWith('이메일 또는 비밀번호가 일치하지 않습니다.');
    });
  });
});