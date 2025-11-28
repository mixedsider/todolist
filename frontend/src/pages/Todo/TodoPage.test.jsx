import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import TodoPage from './TodoPage';
import api from '../../api/axiosConfig';

// 1. Mocking: Router
const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  ...vi.importActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

// 2. Mocking: Axios
vi.mock('../../api/axiosConfig');

// 3. Mocking: Layout
// 주의: TodoPage는 'header' prop으로 로그아웃 버튼을 넘깁니다.
// 테스트에서도 이 헤더를 렌더링해줘야 버튼을 클릭할 수 있습니다.
vi.mock('../../components/MainLayout', () => ({
  default: ({ children, header }) => (
      <div data-testid="main-layout">
        {header}
        {children}
      </div>
  ),
}));

// 테스트용 더미 데이터
const mockTodos = [
  {
    id: 1,
    content: 'React 공부하기',
    isCompleted: false,
    createdAt: '2024-01-01T10:00:00'
  },
  {
    id: 2,
    content: '운동하기',
    isCompleted: true,
    createdAt: '2024-01-01T11:00:00',
    completedAt: '2024-01-01T12:00:00'
  }
];

describe('TodoPage 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks();

    // 기본적으로 목록 조회(GET)는 성공한다고 가정
    api.get.mockResolvedValue({ data: mockTodos });
  });

  it('초기 렌더링 시 할 일 목록과 대시보드 통계가 보여야 한다', async () => {
    render(<TodoPage />);

    // 1. 대시보드 통계 확인 (총 2개, 완료 1개 -> 50%)
    await waitFor(() => {
      expect(screen.getByText('50%')).toBeInTheDocument();
    });
    expect(screen.getByText('My Progress')).toBeInTheDocument();

    // 2. 리스트 항목 확인
    expect(screen.getByText('React 공부하기')).toBeInTheDocument();
    expect(screen.getByText('운동하기')).toBeInTheDocument();
  });

  it('새로운 할 일을 추가하면 API가 호출되어야 한다', async () => {
    // POST 성공 Mock
    api.post.mockResolvedValue({ status: 200 });

    render(<TodoPage />);

    const input = screen.getByPlaceholderText('할 일을 입력하세요...');
    const addButton = screen.getByRole('button', { name: '추가' });

    // 입력 및 클릭
    fireEvent.change(input, { target: { value: '새로운 할일' } });
    fireEvent.click(addButton);

    // API 호출 확인
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/v1/todos', { content: '새로운 할일' });
    });

    // 입력창 초기화 확인
    expect(input.value).toBe('');
  });

  it('완료 체크박스를 누르면 상태 변경 API가 호출되어야 한다', async () => {
    // PATCH 성공 Mock
    api.patch.mockResolvedValue({ status: 200 });

    render(<TodoPage />);
    await waitFor(() => screen.getByText('React 공부하기'));

    // 'React 공부하기' 항목 찾기 (완료 안 된 항목)
    const todoItem = screen.getByText('React 공부하기').closest('li');
    const checkbox = within(todoItem).getByRole('checkbox');

    // 체크박스 클릭
    fireEvent.click(checkbox);

    // API 호출 확인
    await waitFor(() => {
      expect(api.patch).toHaveBeenCalledWith('/v1/todos/1');
    });
  });

  it('삭제 버튼을 누르고 확인(confirm)하면 삭제 API가 호출되어야 한다', async () => {
    // 1. window.confirm Mocking (항상 "확인"을 누른다고 가정)
    const confirmSpy = vi.spyOn(window, 'confirm').mockImplementation(() => true);

    // 2. DELETE 성공 Mock
    api.delete.mockResolvedValue({ status: 204 });

    render(<TodoPage />);
    await waitFor(() => screen.getByText('React 공부하기'));

    // 'React 공부하기'의 삭제 버튼(X) 찾기
    // title 속성을 주었으므로 getByTitle로 찾을 수 있음
    const deleteButtons = screen.getAllByTitle('삭제하기');
    const targetDeleteBtn = deleteButtons[0]; // 첫 번째 항목 삭제

    // 버튼 클릭
    fireEvent.click(targetDeleteBtn);

    // 3. 검증
    expect(confirmSpy).toHaveBeenCalledWith('정말 삭제하시겠습니까?'); // confirm 창 떴는지
    await waitFor(() => {
      expect(api.delete).toHaveBeenCalledWith('/v1/todos/1'); // API 호출 됐는지
    });
  });

  it('로그아웃 버튼을 누르면 API 호출 후 홈으로 이동해야 한다', async () => {
    api.post.mockResolvedValue({ status: 200 });

    render(<TodoPage />);

    // 로그아웃 버튼은 Header에 있음 (MainLayout Mock에서 렌더링됨)
    const logoutButton = screen.getByRole('button', { name: '로그아웃' });
    fireEvent.click(logoutButton);

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/v1/members/logout');
    });
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('세션이 만료된 경우(401) 로그인 페이지로 이동해야 한다', async () => {
    // GET 요청 시 401 에러 발생 설정
    api.get.mockRejectedValue({ response: { status: 401 } });

    // window.alert Mocking
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});

    render(<TodoPage />);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith('세션이 만료되었습니다.');
    });
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });
});