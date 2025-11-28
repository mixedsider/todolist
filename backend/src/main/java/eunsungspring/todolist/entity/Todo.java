package eunsungspring.todolist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "todo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "todo_id")
  private Long id;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private boolean isCompleted; // true: 완료, false: 미완료

  @Column(updatable = false)
  private LocalDateTime createdAt; // 생성 시간

  @Column private LocalDateTime completedAt;

  // 연관관계 매핑 (다대일: 여러 Todo는 하나의 Member에 속함)
  @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 권장
  @JoinColumn(name = "member_id") // 외래키 이름 설정
  private Member member;

  @Builder
  public Todo(String content, Member member) {
    setContent(content);
    this.member = member;
    this.isCompleted = false; // 기본값은 미완료
    this.createdAt = LocalDateTime.now();
  }

  public void toggleStatus() {
    if (!isCompleted) { // 안함 -> 완료
      completedAt = LocalDateTime.now();
      this.isCompleted = !this.isCompleted;
    } else { // 완료 -> 안함
      completedAt = null;
      this.isCompleted = !this.isCompleted;
    }
  }

  public void updateContent(String content) {
    setContent(content);
  }

  private void setContent(String content) {
    if (content.length() < 2) {
      throw new IllegalArgumentException("Content must be at least 2 characters");
    }
    this.content = content;
  }
}
