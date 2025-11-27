import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const TodoPage = () => {
  const [todos, setTodos] = useState([]);      // 할 일 목록 상태
  const [content, setContent] = useState('');  // 입력창 상태
  const navigate = useNavigate();

  // 1. 페이지 로드 시 할 일 목록 가져오기
  useEffect(() => {
    fetchTodos();
  }, []);

  const fetchTodos = async () => {
    try {
      // API: GET /api/v1/todos
      const response = await api.get('/v1/todos');
      setTodos(response.data);
    } catch (error) {
      console.error('목록 조회 실패:', error);
      // 세션이 없거나 만료된 경우 로그인 페이지로 튕겨내기
      if (error.response && error.response.status === 401) {
        alert('로그인이 필요합니다.');
        navigate('/login');
      }
    }
  };

  // 2. 할 일 추가하기
  const handleAddTodo = async (e) => {
    e.preventDefault();

    if (content.length < 2) {
      alert('할 일은 2글자 이상 입력해주세요.');
      return;
    }

    try {
      // API: POST /api/v1/todos
      const response = await api.post('/v1/todos', { content: content });

      if (response.status === 200) {
        // 성공 시 입력창 비우고 목록 다시 불러오기
        setContent('');
        fetchTodos();
      }
    } catch (error) {
      console.error('추가 실패:', error);
      alert('할 일 추가에 실패했습니다.');
    }
  };

  // 3. 할 일 완료 상태 토글 (체크박스 클릭)
  const handleToggle = async (todoId) => {
    try {
      // API: PATCH /api/v1/todos/{todoId}
      const response = await api.patch(`/v1/todos/${todoId}`);

      if (response.status === 200) {
        // 성공 시 화면의 해당 항목 상태만 반대로 뒤집음 (최적화)
        setTodos(todos.map(todo =>
            todo.id === todoId ? { ...todo, isCompleted: !todo.isCompleted } : todo
        ));
      }
    } catch (error) {
      console.error('토글 실패:', error);
    }
  };

  // 4. 로그아웃
  const handleLogout = async () => {
    try {
      // API: POST /api/v1/members/logout
      await api.post('/v1/members/logout');
      alert('로그아웃 되었습니다.');
      navigate('/login');
    } catch (error) {
      console.error('로그아웃 실패:', error);
    }
  };

  return (
      <div style={{ padding: '20px', maxWidth: '500px', margin: '0 auto' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <h2>📝 내 할 일 목록</h2>
          <button onClick={handleLogout} style={{ backgroundColor: '#ff6b6b', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '4px', cursor: 'pointer' }}>
            로그아웃
          </button>
        </div>

        {/* 할 일 입력 폼 */}
        <form onSubmit={handleAddTodo} style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
          <input
              type="text"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="할 일을 입력하세요 (2자 이상)"
              style={{ flex: 1, padding: '8px' }}
          />
          <button type="submit" style={{ padding: '8px 16px' }}>추가</button>
        </form>

        {/* 할 일 리스트 */}
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {todos.map((todo) => (
              <li key={todo.id} style={{
                display: 'flex',
                alignItems: 'center',
                padding: '10px',
                borderBottom: '1px solid #eee',
                textDecoration: todo.isCompleted ? 'line-through' : 'none',
                color: todo.isCompleted ? '#888' : '#000'
              }}>
                <input
                    type="checkbox"
                    checked={todo.isCompleted}
                    onChange={() => handleToggle(todo.id)}
                    style={{ marginRight: '10px', cursor: 'pointer' }}
                />
                <span>{todo.content}</span>
              </li>
          ))}
        </ul>

        {todos.length === 0 && <p style={{ textAlign: 'center', color: '#888' }}>등록된 할 일이 없습니다.</p>}
      </div>
  );
};

export default TodoPage;