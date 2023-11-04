package bobmukjaku.bobmukjakuDemo.domain.member.service;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.Role;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberUpdateDto;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    String PASSWORD = "password";

    private void clear(){
        em.flush();
        em.clear();
    }

    private MemberSignUpDto createMemberSignUpDto(){
        return new MemberSignUpDto("이메일", PASSWORD, "닉네임");
    }

    private MemberSignUpDto setMember() throws Exception{
        MemberSignUpDto memberSignUpDto = createMemberSignUpDto();
        memberService.signUp(memberSignUpDto);
        clear();
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username(memberSignUpDto.memberEmail())
                .password(memberSignUpDto.password())
                .roles(Role.USER.name())
                .build(), null, null));

        SecurityContextHolder.setContext(emptyContext);
        return memberSignUpDto;
    }

    @AfterEach
    public void removeMember(){
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }

    /*
        회원가입
        - 이메일, 닉네임, 비밀번호를 입력하지 않으면 오류
        - 평가 점수, 프로필 색, 인증 날짜는 입력 받지 않고 디폴트 값으로 설정
        - 이미 존재하는 이메일이 있으면 오류
        - 회원가입 후 회원의 Role은 USER
    */

    @Test
    public void 회원가입_성공() throws Exception{
        // given
        MemberSignUpDto memberSignUpDto = createMemberSignUpDto();

        // when
        memberService.signUp(memberSignUpDto);
        clear();

        // then
        Member member = memberRepository.findByMemberEmail(memberSignUpDto.memberEmail()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        assertThat(member.getUid()).isNotNull();
        assertThat(member.getMemberEmail()).isEqualTo(memberSignUpDto.memberEmail());
        assertThat(member.getMemberNickName()).isEqualTo(memberSignUpDto.nickname());
        assertThat(member.getRole()).isSameAs(Role.USER);

    }

    @Test
    public void 회원가입_실패_이메일중복() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = createMemberSignUpDto();
        memberService.signUp(memberSignUpDto);
        clear();

        // when, then
        assertThat(assertThrows(MemberException.class, () -> memberService.signUp(memberSignUpDto)).getExceptionType()).isEqualTo(MemberExceptionType.ALREADY_EXIST_USERNAME);
    }

    @Test
    public void 회원가입_실패_닉네임중복() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = createMemberSignUpDto();
        memberService.signUp(memberSignUpDto);
        clear();

        // when, then
        assertThat(assertThrows(MemberException.class, () -> memberService.signUp(memberSignUpDto)).getExceptionType()).isEqualTo(MemberExceptionType.ALREADY_EXIST_USERNAME);
    }

    @Test
    public void 회원가입_실패_필수입력_필드없으면_오류() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto1 = new MemberSignUpDto(null,passwordEncoder.encode(PASSWORD),"닉네임");
        MemberSignUpDto memberSignUpDto2 = new MemberSignUpDto("이메일",null,"닉네임");
        MemberSignUpDto memberSignUpDto3 = new MemberSignUpDto("이메일",passwordEncoder.encode(PASSWORD),null);

        // when, then

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto1));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto2));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto3));

    }

    /* 회원정보 수정
       - 이메일은 변경 불가능
       - 닉네임, 프로필 색상, 인증날짜, 비밀번호 변경은 2개를 동시에 혹은 선택적으로 변경 가능
       - 변경 사항이 없는데 변경 요청 보내면 오류
       - 회원이 아닌 사용자가 정보 수정 요청 시 오류 -> 시큐리티 필터 동작
     */

    @Test
    public void 닉네임만_수정_성공() throws Exception{
        // given
        MemberSignUpDto memberSignUpDto = setMember();

        // when
        String updateNickName = "수정닉네임";
        memberService.updateMemberInfo(new MemberUpdateDto(Optional.of(updateNickName), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()), SecurityUtil.getLoginUsername());
        clear();
        
        // then
        memberRepository.findByMemberEmail(memberSignUpDto.memberEmail()).ifPresent((member -> {
            assertThat(member.getMemberNickName()).isEqualTo(updateNickName);
        }));
    }

    @Test
    public void 프로필색만_수정_성공() throws Exception{
        // given
        MemberSignUpDto memberSignUpDto = setMember();

        // when
        String updateColor = "bg18";
        memberService.updateMemberInfo(new MemberUpdateDto(Optional.empty(), Optional.of(updateColor), Optional.empty(), Optional.empty(), Optional.empty()), SecurityUtil.getLoginUsername());
        clear();

        // then
        memberRepository.findByMemberEmail(memberSignUpDto.memberEmail()).ifPresent((member -> {
            assertThat(member.getMemberNickName()).isEqualTo(memberSignUpDto.nickname());
            assertThat(member.getProfileColor()).isEqualTo(updateColor);
        }));
    }

    @Test
    public void 닉네임_프로필색_둘다_수정() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = setMember();

        // when
        String updateNickName = "수정닉네임";
        String updateColor = "bg18";
        memberService.updateMemberInfo(new MemberUpdateDto(Optional.of(updateNickName), Optional.of(updateColor), Optional.empty(), Optional.empty(), Optional.empty()), SecurityUtil.getLoginUsername());
        clear();

        // then
        memberRepository.findByMemberEmail(memberSignUpDto.memberEmail()).ifPresent((member -> {
            assertThat(member.getMemberNickName()).isEqualTo(updateNickName);
            assertThat(member.getProfileColor()).isEqualTo(updateColor);
        }));

    }

    @Test
    public void 회원탈퇴_성공() throws Exception{
        // given
        MemberSignUpDto memberSignUpDto = setMember();

        // when
        memberService.withdraw(PASSWORD, SecurityUtil.getLoginUsername());

        // then
        assertThat(assertThrows(Exception.class, ()->
                memberRepository.findByMemberEmail(memberSignUpDto.memberEmail()).orElseThrow(()->
                        new Exception("회원이 아닙니다."))).getMessage()).isEqualTo("회원이 아닙니다.");
    }

    @Test
    public void 회원탈퇴_실패_비밀번호_불일치() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = setMember();

        // when, then
        assertThat(assertThrows(MemberException.class, () -> memberService.withdraw(PASSWORD+"123", SecurityUtil.getLoginUsername())).getExceptionType()).isEqualTo(MemberExceptionType.WRONG_PASSWORD);
    }

    @Test
    public void 회원정보_조회_성공() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = setMember();
        Member member = memberRepository.findByMemberEmail(memberSignUpDto.memberEmail()).orElseThrow(()->
                new Exception());
        clear();

        // when
        MemberInfoDto infoDto = memberService.getInfo(member.getUid());

        // then
        assertThat(infoDto.getMemberEmail()).isEqualTo(memberSignUpDto.memberEmail());
        assertThat(infoDto.getMemberNickName()).isEqualTo(memberSignUpDto.nickname());
    }

    @Test
    public void 내정보조회_성공() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        MemberInfoDto myInfo = memberService.getMyInfo();

        //then
        assertThat(myInfo.getMemberEmail()).isEqualTo(memberSignUpDto.memberEmail());
        assertThat(myInfo.getMemberNickName()).isEqualTo(memberSignUpDto.nickname());

    }
}
