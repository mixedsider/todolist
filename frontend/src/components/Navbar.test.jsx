import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Navbar from './Navbar';

// Router Mocking
const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  ...vi.importActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('Navbar 컴포넌트 테스트', () => {

  it('로고를 클릭하면 홈(/)으로 이동해야 한다', () => {
    render(<Navbar />);

    const logo = screen.getByText('TodoApp');
    fireEvent.click(logo);

    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('로그인 버튼을 클릭하면 /login으로 이동해야 한다', () => {
    render(<Navbar />);

    const loginBtn = screen.getByRole('button', { name: '로그인' });
    fireEvent.click(loginBtn);

    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  it('회원가입 버튼을 클릭하면 /signup으로 이동해야 한다', () => {
    render(<Navbar />);

    const signupBtn = screen.getByRole('button', { name: '회원가입' });
    fireEvent.click(signupBtn);

    expect(mockNavigate).toHaveBeenCalledWith('/signup');
  });
});