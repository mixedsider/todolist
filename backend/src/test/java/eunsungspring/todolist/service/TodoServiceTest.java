package eunsungspring.todolist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eunsungspring.todolist.dto.response.TodoResponse;
import eunsungspring.todolist.entity.Member;
import eunsungspring.todolist.entity.Todo;
import eunsungspring.todolist.repository.MemberRepository;
import eunsungspring.todolist.repository.TodoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

  @Mock TodoRepository todoRepository;

  @Mock MemberRepository memberRepository;

  @InjectMocks TodoService todoService;

  // 테스트용 멤버 생성 도우미 메서드
  private Member createMember(Long id) {
    Member member =
        Member.builder().email("test" + id + "@example.com").password("password@123").build();
    // ID는 DB에서 부여하지만, 테스트에선 직접 주입
    ReflectionTestUtils.setField(member, "id", id);
    return member;
  }

  @Test
  @DisplayName("Todo 생성 성공")
  void createTodo_Success() {
    // given
    Long memberId = 1L;
    Member member = createMember(memberId);

    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

    // when
    todoService.createTodo(memberId, "할 일 내용");

    // then
    verify(todoRepository, times(1)).save(any(Todo.class));
  }

  @Test
  @DisplayName("Todo 생성 실패: 존재하지 않는 회원")
  void createTodo_Fail_MemberNotFound() {
    // given
    Long memberId = 999L;
    given(memberRepository.findById(memberId)).willReturn(Optional.empty());

    // when & then
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              todoService.createTodo(memberId, "할 일 내용");
            });

    assertThat(e.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
  }

  @Test
  @DisplayName("내 Todo 목록 조회 성공")
  void getMyTodos_Success() {
    // given
    Long memberId = 1L;
    Member member = createMember(memberId);

    Todo todo1 = Todo.builder().content("공부하기").member(member).build();
    Todo todo2 = Todo.builder().content("운동하기").member(member).build();
    // ID 주입 (Test용)
    ReflectionTestUtils.setField(todo1, "id", 10L);
    ReflectionTestUtils.setField(todo2, "id", 11L);

    // Repository가 반환할 리스트 설정
    given(todoRepository.findAllByMember_Id(memberId)).willReturn(List.of(todo1, todo2));

    // when
    List<TodoResponse> result = todoService.getMyTodos(memberId);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).content()).isEqualTo("공부하기");
    assertThat(result.get(1).content()).isEqualTo("운동하기");
  }

  @Test
  @DisplayName("Todo 상태 변경 성공 (권한 있음)")
  void toggleTodoStatus_Success() {
    // given
    Long memberId = 1L;
    Long todoId = 100L;
    Member member = createMember(memberId);

    Todo todo = Todo.builder().content("빨래하기").member(member).build();
    ReflectionTestUtils.setField(todo, "id", todoId);

    given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

    // when
    boolean isCompleted = todoService.toggleTodoStatus(todoId, memberId);

    // then
    assertThat(isCompleted).isTrue(); // false -> true 변경 확인
  }

  @Test
  @DisplayName("Todo 상태 변경 실패: 권한 없음 (다른 사람의 Todo)")
  void toggleTodoStatus_Fail_Unauthorized() {
    // given
    Long ownerId = 1L;
    Long otherMemberId = 2L; // 요청한 사람 (해커 혹은 다른 유저)
    Long todoId = 100L;

    Member owner = createMember(ownerId);
    Todo todo = Todo.builder().content("비밀 일기").member(owner).build(); // 주인은 1번
    ReflectionTestUtils.setField(todo, "id", todoId);

    given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

    // when & then
    IllegalStateException e =
        assertThrows(
            IllegalStateException.class,
            () -> {
              // 2번 유저가 1번 유저의 글을 수정 시도
              todoService.toggleTodoStatus(todoId, otherMemberId);
            });

    assertThat(e.getMessage()).isEqualTo("해당 투두에 대한 권한이 없습니다.");
  }
}
