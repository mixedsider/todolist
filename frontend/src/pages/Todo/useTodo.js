// src/pages/Todo/useTodo.js
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const useTodo = () => {
  const [todos, setTodos] = useState([]);
  const [content, setContent] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchTodos();
  }, []);

  const fetchTodos = async () => {
    try {
      const response = await api.get('/v1/todos');
      setTodos(response.data);
    } catch (error) {
      if (error.response && error.response.status === 401) {
        alert('세션이 만료되었습니다.');
        navigate('/login');
      }
    }
  };

  const handleAddTodo = async (e) => {
    e.preventDefault();
    if (content.length < 2) {
      alert('2글자 이상 입력해주세요.');
      return;
    }
    try {
      const response = await api.post('/v1/todos', { content });
      if (response.status === 200) {
        setContent('');
        fetchTodos();
      }
    } catch (error) {
      alert('추가 실패');
    }
  };

  const handleToggle = async (todoId) => {
    try {
      setTodos(prev => prev.map(todo => {
        if (todo.id === todoId) {
          const nextStatus = !todo.isCompleted;
          return {
            ...todo,
            isCompleted: nextStatus,
            completedAt: nextStatus ? new Date().toISOString() : null
          };
        }
        return todo;
      }));
      await api.patch(`/v1/todos/${todoId}`);
      fetchTodos();
    } catch (error) {
      fetchTodos();
    }
  };

  // [NEW] 삭제 기능 추가
  const handleDeleteTodo = async (todoId) => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;

    try {
      // API 호출: DELETE /v1/todos/{todoId}
      const response = await api.delete(`/v1/todos/${todoId}`);

      if (response.status === 204) {
        // 성공 시 로컬 상태에서 바로 제거 (새로고침 없이 반영)
        setTodos(prev => prev.filter(todo => todo.id !== todoId));
      }
    } catch (error) {
      console.error('삭제 실패:', error);
      alert('삭제 중 오류가 발생했습니다.');
    }
  };

  const handleLogout = async () => {
    try {
      await api.post('/v1/members/logout');
      navigate('/');
    } catch (error) {
      console.error(error);
    }
  };

  const handleHomeClick = () => {
    navigate('/');
  }

  const formatDate = (isoString) => {
    if (!isoString) return '';
    const date = new Date(isoString);
    return date.toLocaleString('ko-KR', {
      year: 'numeric', month: 'numeric', day: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });
  };

  const totalCount = todos.length;
  const completedCount = todos.filter(t => t.isCompleted).length;
  const progressRate = totalCount === 0 ? 0 : Math.round((completedCount / totalCount) * 100);

  return {
    todos,
    content,
    setContent,
    totalCount,
    completedCount,
    progressRate,
    handleAddTodo,
    handleToggle,
    handleDeleteTodo, // export 추가
    handleLogout,
    handleHomeClick,
    formatDate
  };
};

export default useTodo;