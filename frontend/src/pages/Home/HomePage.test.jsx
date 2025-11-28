import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import HomePage from './HomePage';

// 1. Mocking: Router (페이지 이동 감지용)
const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  ...vi.importActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

// 2. Mocking: MainLayout (테스트 단순화)
// 레이아웃 내부의 배경 애니메이션 등은 HomePage 테스트의 관심사가 아니므로 단순화합니다.
vi.mock('../../components/MainLayout', () => ({
  default: ({ children }) => <div data-testid="main-layout">{children}</div>,
}));

describe('HomePage 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks(); // 각 테스트 전 Mock 초기화
  });

  it('타이틀과 설명 문구, 버튼들이 정상적으로 렌더링되어야 한다', () => {
    render(<HomePage />);

    // 타이틀 확인 (줄바꿈이 있어서 텍스트 일부로 확인)
    expect(screen.getByText('현대적인 Todo')).toBeInTheDocument();
    expect(screen.getByText('Todo List')).toBeInTheDocument();

    // 뱃지 및 설명글 확인
    expect(screen.getByText('✨ Simple & Powerful')).toBeInTheDocument();
    expect(screen.getByText(/복잡한 일상은 정리하고/i)).toBeInTheDocument();

    // 버튼 확인
    expect(screen.getByRole('button', { name: '시작하기' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '계정 만들기' })).toBeInTheDocument();
  });

  it('"시작하기" 버튼을 누르면 로그인 페이지(/login)로 이동해야 한다', () => {
    render(<HomePage />);

    const startButton = screen.getByRole('button', { name: '시작하기' });
    fireEvent.click(startButton);

    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  it('"계정 만들기" 버튼을 누르면 회원가입 페이지(/signup)로 이동해야 한다', () => {
    render(<HomePage />);

    const signupButton = screen.getByRole('button', { name: '계정 만들기' });
    fireEvent.click(signupButton);

    expect(mockNavigate).toHaveBeenCalledWith('/signup');
  });
});