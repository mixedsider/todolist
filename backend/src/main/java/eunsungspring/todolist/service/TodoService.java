package eunsungspring.todolist.service;

import eunsungspring.todolist.dto.response.TodoResponse;
import eunsungspring.todolist.entity.Member;
import eunsungspring.todolist.entity.Todo;
import eunsungspring.todolist.repository.MemberRepository;
import eunsungspring.todolist.repository.TodoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

  private final TodoRepository todoRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public Long createTodo(Long memberId, String content) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    Todo todo = Todo.builder()
        .content(content)
        .member(member) // 연관관계 설정
        .build();

    todoRepository.save(todo);
    return todo.getId();
  }

  public List<TodoResponse> getMyTodos(Long memberId) {
    List<Todo> todos = todoRepository.findAllByMember_Id(memberId);

    return todos.stream()
        .map(TodoResponse::of)
        .toList();
  }

  @Transactional
  public boolean toggleTodoStatus(Long todoId, Long memberId) {
    Todo todo = todoRepository.findById(todoId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할 일입니다."));


    if (!ObjectUtils.nullSafeEquals(todo.getMember().getId(), memberId)) {
      throw new IllegalStateException("해당 투두에 대한 권한이 없습니다.");
    }

    todo.toggleStatus();

    return todo.isCompleted();
  }

  @Transactional
  public void deleteTodo(Long todoId, Long memberId) {
    Todo todo = todoRepository.findById(todoId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할 일입니다."));

    if (!ObjectUtils.nullSafeEquals(todo.getMember().getId(), memberId)) {
      throw new IllegalStateException("해당 투두에 대한 권한이 없습니다.");
    }

    todoRepository.delete(todo);
  }
}