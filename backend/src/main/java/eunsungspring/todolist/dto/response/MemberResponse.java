package eunsungspring.todolist.dto.response;

import eunsungspring.todolist.entity.Member;

public record MemberResponse(Long id, String email) {

  public static MemberResponse of(Member member) {
    return new MemberResponse(member.getId(), member.getEmail());
  }
}
