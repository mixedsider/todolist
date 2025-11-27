package eunsungspring.todolist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TodoRequest(
    @NotBlank(message = "할 일 내용은 필수입니다.")
    @Size(min = 2, message = "Content must be at least 2 characters")
    String content
) {}