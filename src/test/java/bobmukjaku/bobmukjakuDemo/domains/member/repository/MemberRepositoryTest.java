package bobmukjaku.bobmukjakuDemo.domain.member.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.Role;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
                .memberId("아이디")
                .password("1111")
                .nickName("닉네임1")
                .email("이메일")
                .role(Role.USER)
                .build();
        // when
        Member saveMember = memberRepository.save(member);

        // then
        Member findMember = memberRepository.findById(saveMember.getId()).orElseThrow(()->new RuntimeException("저장된 회원이 없습니다."));

        assertThat(findMember).isSameAs(saveMember);
        assertThat(findMember).isSameAs(member);
    }

    @Test
    public void 예외_회원가입시_아이디_없음() throws Exception{
        // given
        Member member = Member.builder()
                .password("1111")
                .nickName("닉네임1")
                .email("이메일")
                .role(Role.USER)
                .build();

        // when, then
        assertThrows(Exception.class, ()->memberRepository.save(member));
    }

    @Test
    public void 예외_회원가입시_닉네임_없음() throws Exception{
        // given
        Member member = Member.builder()
                .memberId("아이디1")
                .password("1111")
                .email("이메일")
                .role(Role.USER)
                .build();

        // when, then
        assertThrows(Exception.class, ()->memberRepository.save(member));
    }

    @Test
    public void 예외_회원가입시_이메일_없음() throws Exception{
        // given
        Member member = Member.builder()
                .memberId("아이디1")
                .password("1111")
                .nickName("닉네임1")
                .role(Role.USER)
                .build();

        // when, then
        assertThrows(Exception.class, ()->memberRepository.save(member));
    }

    @Test
    public void 예외_회원가입시_중복된_아이디가_있음() throws Exception {
        //given
        Member member1 = Member.builder().memberId("username").password("1234567890").nickName("nickname1").role(Role.USER).build();
        Member member2 = Member.builder().memberId("username").password("1111111111").role(Role.USER).nickName("nickname2").build();

        memberRepository.save(member1);
        clear();

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member2));

    }

    @Test
    void findByMemberId() {
    }

    @Test
    void existsByMemberId() {
    }
}