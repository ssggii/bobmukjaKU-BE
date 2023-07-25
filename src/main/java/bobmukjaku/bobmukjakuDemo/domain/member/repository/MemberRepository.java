package bobmukjaku.bobmukjakuDemo.domain.member.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(String memberId); // 아이디로 회원 찾기
    boolean existsByMemberId(String memberId); // 아이디로 회원 여부 판단

}
