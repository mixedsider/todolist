package eunsungspring.todolist.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eunsungspring.todolist.config.SessionConst;
import eunsungspring.todolist.dto.request.MemberRequest;
import eunsungspring.todolist.dto.response.MemberResponse;
import eunsungspring.todolist.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(MemberController.class) // Controller만 로드하여 테스트
class MemberControllerTest {

  @Autowired MockMvc mockMvc; // 가짜 HTTP 요청을 보내는 객체

  @Autowired ObjectMapper objectMapper; // 객체 -> JSON 변환기

  @MockitoBean MemberService memberService; // 가짜 서비스 (로직은 서비스 테스트에서 검증했으므로 여기선 Mock 처리)

  @Test
  @DisplayName("회원가입 요청 성공")
  void signup_Success() throws Exception {
    // given
    MemberRequest request = new MemberRequest("test@example.com", "password123@");

    // JSON으로 변환
    String jsonContent = objectMapper.writeValueAsString(request);

    // when & then
    mockMvc
        .perform(
            post("/api/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isOk()) // 200 OK 확인
        .andExpect(content().string("회원가입 성공")); // 반환 메시지 확인

    // 서비스가 실제로 호출되었는지 검증
    verify(memberService).signup(any(MemberRequest.class));
  }

  @Test
  @DisplayName("로그인 성공 및 세션 생성 확인")
  void login_Success() throws Exception {
    // given
    MemberRequest request = new MemberRequest("user@example.com", "password123@");

    // 서비스가 반환할 가짜 응답 객체 (Record라고 가정)
    // 실제로는 MemberResponse 구조에 맞춰 생성자를 호출해주세요.
    MemberResponse mockResponse = new MemberResponse(1L, "user@example.com");

    given(memberService.login(any(MemberRequest.class))).willReturn(mockResponse);

    String jsonContent = objectMapper.writeValueAsString(request);

    // when & then
    mockMvc
        .perform(
            post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isOk())
        .andExpect(content().string("로그인 성공"))
        // ★ 중요: 세션에 값이 제대로 들어갔는지 검증
        .andExpect(request().sessionAttribute(SessionConst.LOGIN_MEMBER_ID, 1L));
  }

  @Test
  @DisplayName("로그아웃 성공 및 세션 무효화")
  void logout_Success() throws Exception {
    // given
    // 미리 로그인 된 상태의 세션을 만듦
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, 1L);

    // when
    mockMvc
        .perform(post("/api/v1/members/logout").session(session)) // 세션을 담아서 요청
        .andExpect(status().isOk())
        .andExpect(content().string("로그아웃 성공"));

    // then
    // 세션이 무효화(Invalidate) 되었는지 확인
    assertThat(session.isInvalid()).isTrue();
  }

  @Test
  @DisplayName("로그아웃: 세션이 존재하는 경우 (session != null) -> 세션 무효화 검증")
  void logout_Success_SessionExists() throws Exception {
    // given
    // 1. 가짜 세션(MockHttpSession)을 생성합니다. (이 시점엔 session != null)
    MockHttpSession session = new MockHttpSession();

    // 2. 로그인 된 상태처럼 세션에 값을 넣어둡니다.
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, 1L);

    // when
    mockMvc
        .perform(post("/api/v1/members/logout").session(session)) // 3. 요청에 세션을 담아서 보냅니다.
        .andExpect(status().isOk())
        .andExpect(content().string("로그아웃 성공"));

    // then
    // 4. Controller의 session.invalidate()가 실행되어, 세션이 무효화되었는지 확인합니다.
    assertThat(session.isInvalid()).isTrue();
  }
}
