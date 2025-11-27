package eunsungspring.todolist.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class LoginCheckFilter extends OncePerRequestFilter {

  private static final String[] whiteList = {
      "/api/v1/members/signup",
      "/api/v1/members/login",
      "/css/*", "/js/*", "/favicon.ico",
      "/error" // 스프링 부트 기본 에러 페이지 처리
  };

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String requestURI = request.getRequestURI();

    // 1. 화이트리스트에 포함되거나
    // 2. HTTP 메서드가 OPTIONS인 경우 (CORS 프리플라이트 요청) -> 필터 건너뜀
    return PatternMatchUtils.simpleMatch(whiteList, requestURI)
        || request.getMethod().equals("OPTIONS");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();

    // shouldNotFilter에서 걸러지지 않은 요청만 여기로 들어옵니다. (즉, 로그인 체크가 필요한 요청들)
    try {
      log.info("인증 체크 필터 시작 {}", requestURI);

      HttpSession session = request.getSession(false);

      if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER_ID) == null) {
        log.info("미인증 사용자 요청 {}", requestURI);

        // 401 에러 반환 (React가 이를 감지하여 로그인 페이지로 이동시켜야 함)
        // JSON 형태로 에러 메시지를 내려주고 싶다면 ObjectMapper를 사용해 write 하면 됩니다.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요한 서비스입니다.");
        return; // 다음 필터로 진행하지 않고 끝냄
      }

      // 세션이 있다면 다음 필터로 진행
      filterChain.doFilter(request, response);

    } catch (Exception e) {
      throw e;
    } finally {
      log.info("인증 체크 필터 종료 {}", requestURI);
    }
  }
}