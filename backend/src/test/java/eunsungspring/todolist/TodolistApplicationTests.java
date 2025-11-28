package eunsungspring.todolist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class TodolistApplicationTests {

  @Test
  void contextLoads() {}

  @Test
  @DisplayName("Main 메서드 실행 테스트 (커버리지 100% 달성용)")
  void main() {
    // given
    // 테스트 프로필을 강제로 적용하여 H2 DB를 사용하게 함 (MySQL 연결 에러 방지)
    String[] args = {"--spring.profiles.active=test"};

    // when & then
    // main 메서드를 직접 호출합니다.
    TodolistApplication.main(args);
  }
}
