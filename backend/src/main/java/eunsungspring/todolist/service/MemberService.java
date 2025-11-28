package eunsungspring.todolist.service;

import eunsungspring.todolist.dto.request.MemberRequest;
import eunsungspring.todolist.dto.response.MemberResponse;
import eunsungspring.todolist.entity.Member;
import eunsungspring.todolist.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional
  public Long signup(MemberRequest request) {
    // 중복 회원 검증
    if (request.email() == null || memberRepository.existsByEmail(request.email())) {
      throw new IllegalStateException("이미 존재하는 회원입니다.");
    }

    Member member = Member.builder().email(request.email()).password(request.password()).build();

    memberRepository.save(member);
    return member.getId();
  }

  public MemberResponse login(MemberRequest request) {
    Optional<Member> member =
        memberRepository.findByEmailAndPassword(request.email(), request.password());
    if (member.isEmpty()) {
      throw new IllegalStateException("이메일 혹은 비밀번호를 확인해주세요");
    }

    return MemberResponse.of(member.get());
  }
}
