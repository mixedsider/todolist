package eunsungspring.todolist.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eunsungspring.todolist.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("dev")
class MemberTest {

  @Autowired MemberRepository memberRepository;

  @Test
  @DisplayName("회원가입 성공: 유효한 이메일과 비밀번호 포맷")
  void saveMember_Success() {
    // given
    // 이메일 형식이 맞고, 비밀번호가 8자 이상이며 특수문자(@)를 포함함
    Member member = Member.builder().email("test@example.com").password("password@123").build();

    // when
    Member savedMember = memberRepository.save(member);

    // then
    assertThat(savedMember.getId()).isNotNull();
    assertThat(savedMember.getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("회원가입 실패: 이메일 형식 오류")
  void saveMember_Fail_InvalidEmail() {
    // given & when & then
    // 객체 생성 시점에 검증 로직이 동작하므로, build() 할 때 예외가 터져야 함
    IllegalStateException e =
        assertThrows(
            IllegalStateException.class,
            () -> {
              Member.builder()
                  .email("invalid-email") // @ 없음, 도메인 없음
                  .password("password@123")
                  .build();
            });

    assertThat(e.getMessage()).isEqualTo("Invalid email format");
  }

  @Test
  @DisplayName("회원가입 실패: 비밀번호 길이 부족 (8자 미만)")
  void saveMember_Fail_ShortPassword() {
    // given & when & then
    IllegalStateException e =
        assertThrows(
            IllegalStateException.class,
            () -> {
              Member.builder()
                  .email("test@example.com")
                  .password("pass#1") // 6글자 (실패해야 함)
                  .build();
            });

    assertThat(e.getMessage()).isEqualTo("Invalid password format");
  }

  @Test
  @DisplayName("회원가입 실패: 비밀번호 특수문자 미포함")
  void saveMember_Fail_NoSpecialCharPassword() {
    // given & when & then
    IllegalStateException e =
        assertThrows(
            IllegalStateException.class,
            () -> {
              Member.builder()
                  .email("test@example.com")
                  .password("password123") // 특수문자 없음 (실패해야 함)
                  .build();
            });

    assertThat(e.getMessage()).isEqualTo("Invalid password format");
  }
}
