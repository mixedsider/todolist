package eunsungspring.todolist.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    // Validation Factory를 통해 Validator 인스턴스 생성 (테스트용)
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("유효성 검증 성공: 올바른 이메일과 비밀번호")
  void validate_Success() {
    // given
    MemberRequest request = new MemberRequest("test@example.com", "password@123");

    // when
    Set<ConstraintViolation<MemberRequest>> violations = validator.validate(request);

    // then
    assertThat(violations).isEmpty(); // 에러가 없어야 함
  }

  @Test
  @DisplayName("유효성 검증 실패: 이메일 형식이 아님")
  void validate_Fail_InvalidEmail() {
    // given
    MemberRequest request = new MemberRequest("invalid-email", "password@123");

    // when
    Set<ConstraintViolation<MemberRequest>> violations = validator.validate(request);

    // then
    assertThat(violations).isNotEmpty(); // 에러가 있어야 함
    assertThat(violations)
        .extracting("message")
        .contains("Invalid email format");
  }

  @Test
  @DisplayName("유효성 검증 실패: 비밀번호 패턴 불일치 (특수문자 없음)")
  void validate_Fail_InvalidPassword() {
    // given
    MemberRequest request = new MemberRequest("test@example.com", "password123");

    // when
    Set<ConstraintViolation<MemberRequest>> violations = validator.validate(request);

    // then
    assertThat(violations).isNotEmpty();
    assertThat(violations)
        .extracting("message")
        .contains("Invalid password format");
  }

  @Test
  @DisplayName("유효성 검증 실패: 빈 값 입력 (@NotBlank)")
  void validate_Fail_Blank() {
    // given
    MemberRequest request = new MemberRequest("", "");

    // when
    Set<ConstraintViolation<MemberRequest>> violations = validator.validate(request);

    // then
    assertThat(violations).hasSize(2); // 이메일, 비번 둘 다 걸림
  }
}