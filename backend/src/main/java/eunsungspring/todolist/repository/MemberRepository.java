package eunsungspring.todolist.repository;

import eunsungspring.todolist.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  boolean existsByEmail(String email);
  Optional<Member> findByEmailAndPassword(String email, String password);
}
