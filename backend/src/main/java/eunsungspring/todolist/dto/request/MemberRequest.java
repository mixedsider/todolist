package eunsungspring.todolist.dto.request;

import jakarta.validation.constraints.Pattern;

public record MemberRequest(
    @Pattern(
            regexp =
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message = "Invalid email format")
        String email,
    @Pattern(regexp = "^(?=.*[\\W_]).{8,}$", message = "Invalid password format")
        String password) {}
