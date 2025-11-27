package eunsungspring.todolist.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class LoginCheckFilterTest {

  private LoginCheckFilter filter;

  @BeforeEach
  void setUp() {
    filter = new LoginCheckFilter();
  }

  @Test
  @DisplayName("화이트리스트 요청은 인증 체크 없이 통과해야 한다 (로그인/회원가입)")
  void doFilter_WhiteList_Pass() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v1/members/login"); // 화이트리스트 URL
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    // when
    filter.doFilter(request, response, chain);

    // then
    // 1. 상태 코드가 200이어야 함 (에러 발생 X)
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    // 2. 다음 필터(서블릿)가 호출되어야 함 (MockFilterChain의 요청 개수 확인)
    // OncePerRequestFilter는 shouldNotFilter가 true면 바로 chain.doFilter를 호출함
    assertThat(chain.getRequest()).isNotNull();
  }

  @Test
  @DisplayName("화이트리스트 패턴(*) 요청도 통과해야 한다 (CSS/JS)")
  void doFilter_WhiteList_Pattern_Pass() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/css/style.css"); // /css/* 패턴 매칭
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    // when
    filter.doFilter(request, response, chain);

    // then
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
  }

  @Test
  @DisplayName("OPTIONS 메서드는 인증 체크 없이 통과해야 한다 (CORS 프리플라이트)")
  void doFilter_OptionsMethod_Pass() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v1/todos"); // 인증이 필요한 URL이지만
    request.setMethod("OPTIONS");          // 메서드가 OPTIONS임
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    // when
    filter.doFilter(request, response, chain);

    // then
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
  }

  @Test
  @DisplayName("미인증 요청: 세션이 없으면 401 에러를 반환해야 한다")
  void doFilter_NoSession_Fail() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v1/todos"); // 인증 필요한 URL
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    // 세션 없음 (null)

    // when
    filter.doFilter(request, response, chain);

    // then
    // 1. 상태 코드가 401 Unauthorized여야 함
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);

    // 2. 에러 메시지가 포함되어 있는지 확인 (Servlet 컨테이너 동작 방식에 따라 다를 수 있으나 sendError 메시지 확인)
    assertThat(response.getErrorMessage()).isEqualTo("로그인이 필요한 서비스입니다.");
  }

  @Test
  @DisplayName("미인증 요청: 세션은 있지만 로그인 정보(Attribute)가 없으면 401 에러")
  void doFilter_InvalidSession_Fail() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v1/todos");

    MockHttpSession session = new MockHttpSession(); // 빈 세션 생성
    request.setSession(session);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    // when
    filter.doFilter(request, response, chain);

    // then
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  @DisplayName("인증 요청: 유효한 세션이 있으면 통과해야 한다")
  void doFilter_ValidSession_Pass() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v1/todos"); // 인증 필요한 URL

    MockHttpSession session = new MockHttpSession();
    // 세션에 로그인 회원 ID 저장 (핵심)
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, 1L);
    request.setSession(session);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    // when
    filter.doFilter(request, response, chain);

    // then
    // 통과해서 200 OK
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    // 체인이 연결되었는지 확인 (다음 필터/서블릿 호출됨)
    assertThat(chain.getRequest()).isNotNull();
  }
}