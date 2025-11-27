package eunsungspring.todolist.repository;

import eunsungspring.todolist.entity.Todo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

  List<Todo> findAllByMember_Id(Long memberId);
}
