package eunsungspring.todolist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eunsungspring.todolist.dto.request.MemberRequest;
import eunsungspring.todolist.dto.response.MemberResponse;
import eunsungspring.todolist.entity.Member;
import eunsungspring.todolist.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @Mock MemberRepository memberRepository;

  @InjectMocks MemberService memberService;

  @Test
  @DisplayName("회원가입 성공: 중복되지 않은 이메일")
  void signup_Success() {
    // given
    // Record는 생성자로 데이터를 한 번에 주입합니다.
    MemberRequest request = new MemberRequest("new@example.com", "password@123");

    // getEmail() -> email() 로 변경
    given(memberRepository.existsByEmail(request.email())).willReturn(false);

    // when
    memberService.signup(request);

    // then
    verify(memberRepository, times(1)).save(any(Member.class));
  }

  @Test
  @DisplayName("회원가입 실패: 이미 존재하는 이메일")
  void signup_Fail_DuplicateEmail() {
    // given
    MemberRequest request = new MemberRequest("duplicate@example.com", "password@123");

    // getEmail() -> email()
    given(memberRepository.existsByEmail(request.email())).willReturn(true);

    // when & then
    IllegalStateException e =
        assertThrows(
            IllegalStateException.class,
            () -> {
              memberService.signup(request);
            });

    assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
  }

  @Test
  @DisplayName("회원가입 실패: 이메일이 Null인 경우")
  void signup_Fail_NullEmail() {
    // given
    // 주의: 만약 DTO(Record) 생성자에서 Null 체크를 먼저 하고 있다면,
    // 이 테스트는 DTO 생성 시점에 먼저 예외가 발생할 수 있습니다.
    // 여기서는 Service 로직 테스트를 위해 DTO 검증이 없거나 통과했다고 가정합니다.

    // 억지로 Null을 넣어서 테스트 (혹은 DTO 검증을 잠시 끈 상태 가정)
    // 만약 DTO에 @Valid나 생성자 검증이 있다면 new MemberRequest(null, ..)에서 터질 수 있음
    MemberRequest request = new MemberRequest(null, "password123");

    // when & then
    // existsByEmail을 호출하기 전에, if (email == null) 조건에 걸려 예외가 터져야 함
    assertThatThrownBy(() -> memberService.signup(request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("이미 존재하는 회원입니다.");
  }

  @Test
  @DisplayName("로그인 성공: 이메일과 비밀번호 일치")
  void login_Success() {
    // given
    MemberRequest request = new MemberRequest("user@example.com", "password@123");

    Member member = Member.builder().email("user@example.com").password("password@123").build();

    ReflectionTestUtils.setField(member, "id", 1L);

    // getEmail(), getPassword() -> email(), password()
    given(memberRepository.findByEmailAndPassword(request.email(), request.password()))
        .willReturn(Optional.of(member));

    // when
    MemberResponse response = memberService.login(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.email()).isEqualTo("user@example.com");
  }

  @Test
  @DisplayName("로그인 실패: 잘못된 정보")
  void login_Fail_InvalidCreds() {
    // given
    MemberRequest request = new MemberRequest("wrong@example.com", "wrongpass");

    // getEmail(), getPassword() -> email(), password()
    given(memberRepository.findByEmailAndPassword(request.email(), request.password()))
        .willReturn(Optional.empty());

    // when & then
    IllegalStateException e =
        assertThrows(
            IllegalStateException.class,
            () -> {
              memberService.login(request);
            });

    assertThat(e.getMessage()).isEqualTo("이메일 혹은 비밀번호를 확인해주세요");
  }
}
