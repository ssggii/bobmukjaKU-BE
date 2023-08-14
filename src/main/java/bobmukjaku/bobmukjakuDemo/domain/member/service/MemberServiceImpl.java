package bobmukjaku.bobmukjakuDemo.domain.member.service;

import bobmukjaku.bobmukjakuDemo.domain.chatting.ChatModel;
import bobmukjaku.bobmukjakuDemo.domain.chatting.ProfanityResponse;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.HashedAuthCodeDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberUpdateDto;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.global.jwt.service.JwtService;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthService emailAuthService;

    @Override
    public void signUp(MemberSignUpDto memberSignUpDto) throws Exception {

        // 회원가입 여부 체크
        if (memberRepository.findByMemberEmail(memberSignUpDto.memberEmail()).isPresent()){
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_USERNAME);
        }

        Member member = memberSignUpDto.toEntity();
        member.giveUserAuthority();
        member.encodePassword(passwordEncoder);

        memberRepository.save(member);
    }

    @Override
    public List<MemberInfoDto> getAllMembers() throws Exception {
        List<Member> allMembers = memberRepository.findAll();
        List<MemberInfoDto> result = allMembers.stream().map(MemberInfoDto::new).collect(Collectors.toList());
        return result;
    }

    @Override
    public MemberInfoDto getInfo(Long id) throws Exception {
        Member findMember = memberRepository.findById(id).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        return new MemberInfoDto(findMember);
    }

    @Override
    public MemberInfoDto getMyInfo() throws Exception {
        Member findMember = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        return new MemberInfoDto(findMember);
    }

    @Override
    public void updateMemberInfo(MemberUpdateDto memberUpdateDto, String username) throws Exception {
        Member member = memberRepository.findByMemberEmail(username)
                .orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        memberUpdateDto.nickName().ifPresent(member::updateNickName);
        memberUpdateDto.profileColor().ifPresent(member::updateProfileColor);
        memberUpdateDto.certificatedAt().ifPresent(member::updateCertificatedAt);
        memberUpdateDto.rate().ifPresent(member::updateRate);
        memberUpdateDto.toBePassword().ifPresent(password -> member.updatePassword(passwordEncoder, password));
    }

    @Override
    public void withdraw(String checkPassword, String username) throws Exception {
        Member member = memberRepository.findByMemberEmail(username)
                .orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        if(!member.matchPassword(passwordEncoder, checkPassword)){
            throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        memberRepository.delete(member);
    }

    @Override
    public boolean checkNickName(String nickName) throws Exception {
        boolean result = memberRepository.existsByMemberNickName(nickName);
        return result;
    }

    @Override
    public HashedAuthCodeDto mailAuth(String email) throws Exception {
        String authcode = emailAuthService.createAuthCode();
        emailAuthService.sendMail(authcode, email);
        System.out.println("메일인증~~");
        return emailAuthService.hashAuthCode(authcode);
    }

}
