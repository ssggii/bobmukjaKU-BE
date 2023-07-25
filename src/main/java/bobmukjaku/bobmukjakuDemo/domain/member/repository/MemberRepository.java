package bobmukjaku.bobmukjakuDemo.domain.member.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberEmail(String memberEmail); // 이메일로 회원 찾기
    boolean existsByMemberEmail(String memberEmail); // 이메일로 회원 여부 판단
    boolean ifCertificated(String certificatedAt); // 재학생 인증 여부 판단

}
