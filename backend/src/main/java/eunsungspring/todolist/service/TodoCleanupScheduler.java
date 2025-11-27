package eunsungspring.todolist.service;

import eunsungspring.todolist.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodoCleanupScheduler {

  private final TodoRepository todoRepository;

  // "0 0 0 * * *" -> 매일 00시 00분 00초에 실행
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  @Transactional // 삭제는 DB 변경이 일어나므로 트랜잭션 필수
  public void cleanupTodos() {
    log.info("⏰ 자정이 되었습니다. 모든 할 일을 삭제합니다.");

    try {
      todoRepository.deleteAllInBatch();

      log.info("오늘 이전 데이터 삭제 완료");
    } catch (Exception e) {
      log.error("❌ 삭제 중 오류 발생", e);
    }
  }
}
