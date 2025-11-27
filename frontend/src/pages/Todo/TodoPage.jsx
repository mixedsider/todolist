// src/pages/Todo/TodoPage.jsx
import React from 'react';
import MainLayout from '../../components/MainLayout';
import useTodo from './useTodo';
import styles from './TodoPage.module.css';

const TodoPage = () => {
  const {
    todos,
    content,
    setContent,
    totalCount,
    completedCount,
    progressRate,
    handleAddTodo,
    handleToggle,
    handleDeleteTodo, // [NEW] 핸들러 가져오기
    handleLogout,
    handleHomeClick,
    formatDate
  } = useTodo();

  const DashboardHeader = (
      <header className={styles.header}>
        <div className={styles.headerInner}>
          <div className={styles.logo} onClick={handleHomeClick}>TodoApp</div>
          <div className={styles.userInfo}>
            <button onClick={handleLogout} className={styles.logoutButton}>로그아웃</button>
          </div>
        </div>
      </header>
  );

  return (
      <MainLayout
          header={DashboardHeader}
          enableAnimation={false}
          backgroundColor="#f8fafc"
          align="top"
      >
        <div className={styles.gridContainer}>

          {/* 좌측 패널 (대시보드) - 변경 없음 */}
          <aside className={styles.leftPanel}>
            <div className={styles.dashboardCard}>
              <h3 className={styles.cardTitle}>My Progress</h3>
              <div className={styles.progressCircleArea}>
                <span className={styles.bigPercent}>{progressRate}%</span>
                <span className={styles.progressLabel}>완료됨</span>
              </div>
              <div className={styles.progressBarBg}>
                <div className={styles.progressBarFill} style={{ width: `${progressRate}%` }}></div>
              </div>
              <div className={styles.statRow}>
                <div className={styles.statItem}>
                  <span className={styles.statValue}>{totalCount}</span>
                  <span className={styles.statLabel}>전체</span>
                </div>
                <div className={styles.divider}></div>
                <div className={styles.statItem}>
                  <span className={styles.statValueDone}>{completedCount}</span>
                  <span className={styles.statLabel}>완료</span>
                </div>
                <div className={styles.divider}></div>
                <div className={styles.statItem}>
                  <span className={styles.statValueActive}>{totalCount - completedCount}</span>
                  <span className={styles.statLabel}>진행중</span>
                </div>
              </div>
            </div>
            <div className={`${styles.dashboardCard} ${styles.motivationCard}`}>
              <p className={styles.motivationText}>
                {progressRate === 100 ? "완벽해요! 🎉" :
                    progressRate > 50 ? "거의 다 왔어요! 🔥" :
                        "시작해볼까요? 🌱"}
              </p>
            </div>
          </aside>

          {/* 우측 패널 (리스트) */}
          <section className={styles.rightPanel}>
            <div className={styles.inputCard}>
              <form onSubmit={handleAddTodo} className={styles.form}>
                <input
                    type="text"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    placeholder="할 일을 입력하세요..."
                    className={styles.input}
                />
                <button type="submit" className={styles.addButton}>추가</button>
              </form>
            </div>

            <div className={styles.listContainer}>
              {todos.length === 0 ? (
                  <div className={styles.emptyState}>등록된 할 일이 없습니다.</div>
              ) : (
                  <ul className={styles.list}>
                    {todos.map((todo) => (
                        <li key={todo.id} className={todo.isCompleted ? styles.itemCompleted : styles.item}>

                          {/* 내용 클릭 시 완료 토글 */}
                          <label className={styles.itemContent}>
                            <input
                                type="checkbox"
                                checked={todo.isCompleted}
                                onChange={() => handleToggle(todo.id)}
                                className={styles.checkbox}
                            />
                            <span className={styles.todoText}>{todo.content}</span>
                          </label>

                          {/* 날짜 정보 */}
                          <div className={styles.dateInfo}>
                            {todo.isCompleted ? (
                                <span className={styles.dateCompleted}>
                          완료: {formatDate(todo.completedAt || new Date().toISOString())}
                        </span>
                            ) : (
                                <span className={styles.dateCreated}>
                          {formatDate(todo.createdAt)}
                        </span>
                            )}
                          </div>

                          {/* [NEW] 삭제 버튼 (빨간색 X) */}
                          <button
                              className={styles.deleteButton}
                              onClick={() => handleDeleteTodo(todo.id)}
                              title="삭제하기"
                          >
                            ✕
                          </button>
                        </li>
                    ))}
                  </ul>
              )}
            </div>
          </section>
        </div>
      </MainLayout>
  );
};

export default TodoPage;