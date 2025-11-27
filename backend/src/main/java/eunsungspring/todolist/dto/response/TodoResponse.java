package eunsungspring.todolist.dto.response;

import eunsungspring.todolist.entity.Todo;
import java.time.LocalDateTime;

public record TodoResponse(Long id, String content, boolean isCompleted, LocalDateTime createdAt, LocalDateTime completedAt) {

  public static TodoResponse of(Todo todo) {
    return new TodoResponse(todo.getId(), todo.getContent(), todo.isCompleted(), todo.getCreatedAt(), todo.getCompletedAt());
  }

}