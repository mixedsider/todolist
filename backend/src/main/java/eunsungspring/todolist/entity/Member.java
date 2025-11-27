package eunsungspring.todolist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member") // 테이블 이름 지정
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 보호
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL의 Auto Increment 사용
  @Column(name = "member_id")
  private Long id;

  @Column(nullable = false, unique = true) // 이메일은 중복 불가
  private String email;

  @Column(nullable = false)
  private String password;

  // 생성자 (Builder 패턴 사용 권장)
  @Builder
  public Member(String email, String password) {
    setEmail(email);
    setPassword(password);
  }

  private void setEmail(String email) {
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    if(!Pattern.matches(emailRegex, email)) {
      throw new IllegalStateException("Invalid email format");
    }
    this.email = email;
  }

  private void setPassword(String password) {
    // 비밀번호는 최소 8글자에 특수 문자 한글자를 포함해야된다.
    String passwordRegex = "^(?=.*[\\W_]).{8,}$";
    if(!Pattern.matches(passwordRegex, password)) {
      throw new IllegalStateException("Invalid password format");
    }
    this.password = password;
  }
}