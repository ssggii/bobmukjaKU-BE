package bobmukjaku.bobmukjakuDemo.domain.member.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberEmail(String memberEmail); // 이메일로 회원 조회
    boolean existsByMemberEmail(String memberEmail); // 이메일로 회원 여부 판단
    boolean existsByMemberNickName(String nickName); // 닉네임으로 회원 여부 판단
    Optional<Member> findByRefreshToken(String refreshToken); // refreshToken으로 회원 조회

}
