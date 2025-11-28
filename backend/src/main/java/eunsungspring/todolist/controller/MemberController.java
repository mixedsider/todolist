package eunsungspring.todolist.controller;

import eunsungspring.todolist.config.SessionConst;
import eunsungspring.todolist.dto.request.MemberRequest;
import eunsungspring.todolist.dto.response.MemberResponse;
import eunsungspring.todolist.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

  private final MemberService memberService;

  // 회원가입
  @PostMapping("/v1/members/signup")
  public ResponseEntity<String> signup(@Valid @RequestBody MemberRequest request) {
    memberService.signup(request);
    return ResponseEntity.ok("회원가입 성공");
  }

  // 로그인
  @PostMapping("/v1/members/login")
  public ResponseEntity<String> login(
      @Valid @RequestBody MemberRequest request, HttpServletRequest httpRequest) {
    MemberResponse loginMember = memberService.login(request);

    HttpSession session = httpRequest.getSession(true);
    // 세션에 로그인 회원 ID 보관
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, loginMember.id());

    return ResponseEntity.ok("로그인 성공");
  }

  // 로그아웃
  @PostMapping("/v1/members/logout")
  public ResponseEntity<String> logout(HttpServletRequest httpRequest) {
    HttpSession session = httpRequest.getSession(false);
    session.invalidate(); // 세션 초기화
    return ResponseEntity.ok("로그아웃 성공");
  }
}
