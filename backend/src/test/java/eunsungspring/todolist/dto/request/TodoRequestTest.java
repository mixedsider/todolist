package eunsungspring.todolist.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TodoRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("유효성 검증 성공: 2글자 이상")
  void validate_Success() {
    // given
    TodoRequest request = new TodoRequest("운동하기");

    // when
    Set<ConstraintViolation<TodoRequest>> violations = validator.validate(request);

    // then
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("유효성 검증 실패: 2글자 미만")
  void validate_Fail_ShortContent() {
    // given
    TodoRequest request = new TodoRequest("밥");

    // when
    Set<ConstraintViolation<TodoRequest>> violations = validator.validate(request);

    // then
    assertThat(violations).isNotEmpty();
    assertThat(violations).extracting("message").contains("Content must be at least 2 characters");
  }

  @Test
  @DisplayName("유효성 검증 실패: 공백만 입력 (@NotBlank)")
  void validate_Fail_BlankContent() {
    // given
    // @NotBlank는 null과 "", " " 모두 잡아냅니다.
    TodoRequest request = new TodoRequest("   ");

    // when
    Set<ConstraintViolation<TodoRequest>> violations = validator.validate(request);

    // then
    assertThat(violations).isNotEmpty();
    assertThat(violations).extracting("message").contains("할 일 내용은 필수입니다.");
  }
}
