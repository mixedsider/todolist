package eunsungspring.todolist.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eunsungspring.todolist.config.SessionConst;
import eunsungspring.todolist.dto.request.TodoRequest;
import eunsungspring.todolist.dto.response.TodoResponse;
import eunsungspring.todolist.service.TodoService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean TodoService todoService;

  @Test
  @DisplayName("내 Todo 목록 조회 성공 (로그인 상태)")
  void getMyTodos_Success() throws Exception {
    // given
    Long memberId = 1L;

    // 가짜 세션 생성 및 회원 ID 주입
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);

    // 서비스가 반환할 데이터 준비
    List<TodoResponse> mockTodos =
        List.of(
            new TodoResponse(10L, "스프링 공부", false, LocalDateTime.now(), null),
            new TodoResponse(11L, "운동하기", true, LocalDateTime.now(), null));
    given(todoService.getMyTodos(memberId)).willReturn(mockTodos);

    // when & then
    mockMvc
        .perform(get("/api/v1/todos").session(session)) // 세션 포함 요청
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2)) // 리스트 크기 확인
        .andExpect(jsonPath("$[0].content").value("스프링 공부"))
        .andExpect(jsonPath("$[1].isCompleted").value(true));
  }

  @Test
  @DisplayName("내 Todo 목록 조회 실패 (비로그인 - 401 Unauthorized)")
  void getMyTodos_Fail_Unauthorized() throws Exception {
    // given
    // 세션 없이 요청

    // when & then
    mockMvc.perform(get("/api/v1/todos")).andExpect(status().isUnauthorized()); // 401 확인
  }

  @Test
  @DisplayName("Todo 생성 성공")
  void createTodo_Success() throws Exception {
    // given
    Long memberId = 1L;
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);

    // Record 사용 가정 (TodoRequest)
    TodoRequest request = new TodoRequest("새로운 할 일");
    String jsonContent = objectMapper.writeValueAsString(request);

    // 서비스가 ID 100을 반환한다고 가정
    given(todoService.createTodo(eq(memberId), any())).willReturn(100L);

    // when & then
    mockMvc
        .perform(
            post("/api/v1/todos")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isOk())
        .andExpect(content().string("100")); // 반환된 ID 확인

    verify(todoService).createTodo(eq(memberId), eq("새로운 할 일"));
  }

  @Test
  @DisplayName("Todo 상태 변경 성공")
  void toggleStatus_Success() throws Exception {
    // given
    Long memberId = 1L;
    Long todoId = 50L;
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);

    // 서비스가 true(완료됨)를 반환한다고 가정
    given(todoService.toggleTodoStatus(todoId, memberId)).willReturn(true);

    // when & then
    mockMvc
        .perform(patch("/api/v1/todos/{todoId}", todoId).session(session))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  @DisplayName("Todo 상태 변경 실패 (비로그인)")
  void toggleStatus_Fail_Unauthorized() throws Exception {
    // given
    Long todoId = 50L;
    // 세션 없음

    // when & then
    mockMvc.perform(patch("/api/v1/todos/{todoId}", todoId)).andExpect(status().isUnauthorized());
  }
}
