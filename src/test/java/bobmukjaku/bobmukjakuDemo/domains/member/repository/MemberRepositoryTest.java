package bobmukjaku.bobmukjakuDemo.domains.member.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.Role;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    private void clear(){
        em.flush();
        em.clear();
    }

    @AfterEach
    private void after(){
        em.clear();
    }

    @Test
    public void 회원저장_성공() throws Exception{
        // given
        Member member = Member.builder()
                .memberEmail("이메일1")
                .memberPassword("비밀번호1")
                .memberNickName("닉네임1")
                .role(Role.USER)
                .build();
        // when
        Member saveMember = memberRepository.save(member);

        // then
        Member findMember = memberRepository.findById(saveMember.getUid())
                .orElseThrow(()->new RuntimeException("저장된 회원이 없습니다."));

        assertThat(findMember).isSameAs(saveMember);
        assertThat(findMember).isSameAs(member);
    }

    @Test
    public void 예외_회원가입시_닉네임_없음() throws Exception{
        // given
        Member member = Member.builder()
                .memberEmail("이메일1")
                .memberPassword("비밀번호1")
                .role(Role.USER)
                .build();

        // when, then
        assertThrows(Exception.class, ()->memberRepository.save(member));
    }

    @Test
    public void 예외_회원가입시_이메일_없음() throws Exception{
        // given
        Member member = Member.builder()
                .memberPassword("비밀번호1")
                .memberNickName("닉네임1")
                .role(Role.USER)
                .build();

        // when, then
        assertThrows(Exception.class, ()->memberRepository.save(member));
    }

    @Test
    public void 예외_회원가입시_이메일_중복() throws Exception {
        //given
        Member member1 = Member.builder().memberEmail("이메일").memberPassword("비밀번호1").memberNickName("닉네임1").role(Role.USER).build();
        Member member2 = Member.builder().memberEmail("이메일").memberPassword("비밀번호2").memberNickName("닉네임2").role(Role.USER).build();

        memberRepository.save(member1);
        clear();

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member2));

    }

    @Test
    public void 예외_회원가입시_닉네임_중복() throws Exception {
        //given
        Member member1 = Member.builder().memberEmail("이메일1").memberPassword("비밀번호1").memberNickName("닉네임").role(Role.USER).build();
        Member member2 = Member.builder().memberEmail("이메일2").memberPassword("비밀번호2").memberNickName("닉네임").role(Role.USER).build();

        memberRepository.save(member1);
        clear();

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member2));

    }

    @Test
    public void 성공_회원수정() throws Exception {
        //given
        Member member1 = Member.builder().memberEmail("이메일").memberPassword("비밀번호").memberNickName("닉네임").role(Role.USER).build();
        memberRepository.save(member1);
        clear();

        String updatePassword = "updatePassword";
        String updateNickName = "updateNickName";
        int updateRate = 50;
        String updateProfileColor = "bg15";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //when
        Member findMember = memberRepository.findById(member1.getUid()).orElseThrow(() -> new Exception());
        findMember.updateNickName(updateNickName);
        findMember.updatePassword(passwordEncoder,updatePassword);
        findMember.updateRate(updateRate);
        findMember.updateProfileColor(updateProfileColor);
        em.flush();

        //then
        Member findUpdateMember = memberRepository.findById(findMember.getUid()).orElseThrow(() -> new Exception());

        assertThat(findUpdateMember).isSameAs(findMember);
        assertThat(passwordEncoder.matches(updatePassword, findUpdateMember.getMemberPassword())).isTrue();
        assertThat(findUpdateMember.getMemberNickName()).isEqualTo(updateNickName);
        assertThat(findUpdateMember.getRate()).isNotEqualTo(member1.getRate());
        assertThat(findUpdateMember.getProfileColor()).isNotEqualTo(member1.getProfileColor());
    }

    @Test
    public void 성공_회원삭제() throws Exception {
        //given
        Member member1 = Member.builder().memberEmail("이메일").memberPassword("비밀번호").memberNickName("닉네임").role(Role.USER).build();
        memberRepository.save(member1);
        clear();

        //when
        memberRepository.delete(member1);
        clear();

        //then
        assertThrows(Exception.class,
                () -> memberRepository.findById(member1.getUid())
                        .orElseThrow(() -> new Exception()));
    }

    @Test
    void findByMemberEmail_성공() {
        //given
        String memberEmail = "이메일";
        Member member1 = Member.builder().memberEmail("이메일").memberPassword("비밀번호").memberNickName("닉네임").role(Role.USER).build();
        memberRepository.save(member1);
        clear();

        //when, then
        assertThat(memberRepository.findByMemberEmail(memberEmail).get().getUid()).isEqualTo(member1.getUid());
        assertThat(memberRepository.findByMemberEmail(memberEmail).get().getMemberNickName()).isEqualTo(member1.getMemberNickName());
        assertThat(memberRepository.findByMemberEmail(memberEmail).get().getCertificatedAt()).isEqualTo(member1.getCertificatedAt());
        assertThrows(Exception.class,
                () -> memberRepository.findByMemberEmail(memberEmail+"123")
                        .orElseThrow(() -> new Exception()));
    }

    @Test
    void existsByMemberEmail_성공() {
        //given
        String memberEmail = "이메일";
        Member member1 = Member.builder().memberEmail("이메일").memberPassword("비밀번호").memberNickName("닉네임").role(Role.USER).build();
        memberRepository.save(member1);
        clear();

        //when, then
        assertThat(memberRepository.existsByMemberEmail(memberEmail)).isTrue();
        assertThat(memberRepository.existsByMemberEmail(memberEmail+"123")).isFalse();
    }

    @Test
    public void 회원가입시_날짜_정보_생성() throws Exception {
        //given
        Member member1 = Member.builder().memberEmail("이메일").memberPassword("비밀번호").memberNickName("닉네임").role(Role.USER).build();
        memberRepository.save(member1);
        clear();

        //when
        Member findMember = memberRepository.findById(member1.getUid()).orElseThrow(() -> new Exception());

        //then
        assertThat(findMember.getCreatedAt()).isNotNull(); // 생성날짜
        assertThat(findMember.getLastModifiedAt()).isNotNull(); // 수정 날짜
        assertThat(findMember.getCertificatedAt()).isNotNull(); // 인증 날짜

    }
}