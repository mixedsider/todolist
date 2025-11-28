import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import MainLayout from './MainLayout';

// 1. 자식 컴포넌트 Mocking (테스트 격리)
// Navbar나 배경 애니메이션 내부 로직은 여기서 테스트할 필요가 없습니다.
vi.mock('./Navbar', () => ({
  default: () => <div data-testid="mock-navbar">기본 네비게이션</div>,
}));

vi.mock('./AnimatedBackground', () => ({
  default: () => <div data-testid="mock-bg">배경 애니메이션</div>,
}));

describe('MainLayout 컴포넌트 테스트', () => {

  it('기본 설정으로 렌더링 시 네비게이션, 배경, 자식 요소가 모두 보여야 한다', () => {
    render(
        <MainLayout>
          <div data-testid="child-content">메인 컨텐츠</div>
        </MainLayout>
    );

    expect(screen.getByTestId('mock-navbar')).toBeInTheDocument(); // 기본 헤더
    expect(screen.getByTestId('mock-bg')).toBeInTheDocument();     // 기본 배경
    expect(screen.getByTestId('child-content')).toBeInTheDocument(); // 컨텐츠
  });

  it('enableAnimation이 false면 배경 애니메이션이 없어야 한다', () => {
    render(
        <MainLayout enableAnimation={false}>
          <div>컨텐츠</div>
        </MainLayout>
    );

    // queryBy...는 요소가 없으면 null을 반환합니다 (getBy...는 에러 발생)
    expect(screen.queryByTestId('mock-bg')).not.toBeInTheDocument();
  });

  it('커스텀 header를 전달하면 기본 Navbar 대신 커스텀 헤더가 보여야 한다', () => {
    const CustomHeader = <div data-testid="custom-header">나만의 헤더</div>;

    render(
        <MainLayout header={CustomHeader}>
          <div>컨텐츠</div>
        </MainLayout>
    );

    expect(screen.getByTestId('custom-header')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-navbar')).not.toBeInTheDocument(); // 기본 헤더는 없어야 함
  });

  it('backgroundColor와 align 속성이 스타일로 적용되어야 한다', () => {
    const { container } = render(
        <MainLayout backgroundColor="#ff0000" align="top">
          <div>컨텐츠</div>
        </MainLayout>
    );

    // 최상위 div 컨테이너의 스타일 확인
    // container.firstChild는 MainLayout의 최상위 div를 가리킵니다.
    const wrapper = container.firstChild;

    // 배경색 확인
    expect(wrapper).toHaveStyle({ backgroundColor: '#ff0000' });

    // main 태그의 정렬 스타일 확인
    const main = screen.getByRole('main');
    expect(main).toHaveStyle({
      justifyContent: 'flex-start', // align="top"일 때
      paddingTop: '40px'
    });
  });
});