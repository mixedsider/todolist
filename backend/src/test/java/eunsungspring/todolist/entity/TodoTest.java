package eunsungspring.todolist.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eunsungspring.todolist.repository.MemberRepository;
import eunsungspring.todolist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class TodoRepositoryTest {

  @Autowired TodoRepository todoRepository;

  @Autowired MemberRepository memberRepository;

  private Member savedMember;

  // 각 테스트 실행 전에 유효한 멤버를 미리 생성해서 DB에 저장해둠
  @BeforeEach
  void setUp() {
    Member member =
        Member.builder()
            .email("test@example.com") // 유효한 이메일
            .password("password@123") // 유효한 비밀번호 (8자 이상 + 특수문자)
            .build();
    savedMember = memberRepository.save(member);
  }

  @Test
  @DisplayName("Todo 저장 성공: 정상적인 내용과 멤버 연결")
  void saveTodo_Success() {
    // given
    Todo todo =
        Todo.builder()
            .content("스프링 공부하기") // 2글자 이상
            .member(savedMember)
            .build();

    // when
    Todo savedTodo = todoRepository.save(todo);

    // then
    assertThat(savedTodo.getId()).isNotNull();
    assertThat(savedTodo.getContent()).isEqualTo("스프링 공부하기");
    assertThat(savedTodo.isCompleted()).isFalse(); // 기본값 false 확인
    assertThat(savedTodo.getCreatedAt()).isNotNull(); // 생성 시간 존재 확인
    assertThat(savedTodo.getMember()).isEqualTo(savedMember); // 연관관계 확인
  }

  @Test
  @DisplayName("Todo 생성 실패: 내용이 2글자 미만일 때")
  void saveTodo_Fail_ShortContent() {
    // given & when & then
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              Todo.builder()
                  .content("밥") // 1글자 (실패해야 함)
                  .member(savedMember)
                  .build();
            });

    assertThat(e.getMessage()).isEqualTo("Content must be at least 2 characters");
  }

  @Test
  @DisplayName("Todo 수정 실패: 수정할 내용이 2글자 미만일 때")
  void updateTodo_Fail_ShortContent() {
    // given
    Todo todo = Todo.builder().content("운동하기").member(savedMember).build();
    todoRepository.save(todo);

    // when & then
    // updateContent 메서드도 내부적으로 setContent를 호출하므로 예외가 터져야 함
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              todo.updateContent("쉼"); // 1글자로 수정 시도
            });

    assertThat(e.getMessage()).isEqualTo("Content must be at least 2 characters");
  }

  @Test
  @DisplayName("비즈니스 로직: 완료 상태 토글 (미완료 -> 완료 -> 미완료)")
  void toggleStatus_Logic() {
    // given
    Todo todo = Todo.builder().content("빨래하기").member(savedMember).build();
    Todo savedTodo = todoRepository.save(todo);

    // when 1: 처음 토글 (false -> true)
    savedTodo.toggleStatus();

    // then 1
    assertThat(savedTodo.isCompleted()).isTrue();

    // when 2: 다시 토글 (true -> false)
    savedTodo.toggleStatus();

    // then 2
    assertThat(savedTodo.isCompleted()).isFalse();
  }
}
