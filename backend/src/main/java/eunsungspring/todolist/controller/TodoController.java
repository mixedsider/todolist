package eunsungspring.todolist.controller;

import eunsungspring.todolist.config.SessionConst;
import eunsungspring.todolist.dto.request.TodoRequest;
import eunsungspring.todolist.dto.response.TodoResponse;
import eunsungspring.todolist.service.TodoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TodoController {

  private final TodoService todoService;

  // 내 Todo 목록 조회
  @GetMapping("/v1/todos")
  public ResponseEntity<List<TodoResponse>> getMyTodos(
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER_ID) Long memberId) {

    List<TodoResponse> myTodos = todoService.getMyTodos(memberId);
    return ResponseEntity.ok(myTodos);
  }

  // Todo 생성
  @PostMapping("/v1/todos")
  public ResponseEntity<Long> createTodo(
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER_ID) Long memberId,
      @Valid @RequestBody TodoRequest request) {

    Long todoId = todoService.createTodo(memberId, request.content());
    return ResponseEntity.ok(todoId);
  }

  // Todo 상태 변경 (완료 <-> 미완료)
  @PatchMapping("/v1/todos/{todoId}")
  public ResponseEntity<Boolean> toggleStatus(
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER_ID) Long memberId,
      @Positive @PathVariable Long todoId) {

    boolean result = todoService.toggleTodoStatus(todoId, memberId);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/v1/todos/{todoId}")
  public ResponseEntity<Void> deleteTodo(
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER_ID) Long memberId,
      @Positive @PathVariable Long todoId ) {
    todoService.deleteTodo(todoId, memberId);
    return ResponseEntity.noContent().build();
  }

}