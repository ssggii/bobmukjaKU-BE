package bobmukjaku.bobmukjakuDemo.domain.member.service;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.TimeBlock;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.*;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.TimeBlockRepository;
import bobmukjaku.bobmukjakuDemo.domain.memberchatroom.MemberChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.place.Review;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final TimeBlockRepository timeBlockRepository;
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
    public void withdrawMember(String username) throws Exception {
        Member member = memberRepository.findByMemberEmail(username)
                .orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        for(Review review : member.getReviewList()){
            review.setWriter(null);
        }
        for(MemberChatRoom memberChatRoom : member.getJoiningRooms()){
            memberChatRoom.getChatRoom().deleteParticipant(memberChatRoom);
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

    @Override
    public void updateTimeBlock(List<TimeBlockDto> timeBlockDtoList) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<TimeBlock> timeBlocks = timeBlockDtoList.stream().map(timeBlockDto -> timeBlockDto.toEntity(member)).collect(Collectors.toList());
//        timeBlockRepository.saveAll(timeBlocks);

        List<Long> toDeleteIds = member.getTimeBlockList().stream().map(timeBlock -> timeBlock.getTimeBlockId()).collect(Collectors.toList());
        if(toDeleteIds != null && !toDeleteIds.isEmpty()){
            for(Long id : toDeleteIds)
                timeBlockRepository.deleteById(id);
        }

        member.updateTimeBlockInfo(timeBlocks);
    }

    @Override
    public List<TimeBlockDto> getMyTimeBlocks() throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<TimeBlockDto> timeBlockDtoList = member.getTimeBlockList().stream().map(timeBlock -> timeBlock.toDto(timeBlock)).collect(Collectors.toList());
        return timeBlockDtoList;
    }

    @Override
    public void rateUpdate(Long id, Integer score) throws Exception {
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        if((member.getRate() == 100 && score == 1)
        || (member.getRate() == 0 && score == -1))return;
        member.updateRate(member.getRate() + score);
    }

    @Override
    public void resetMemberPassword(PasswordUpdateDto passwordUpdateDto) {
        Member member = memberRepository.findByMemberEmail(passwordUpdateDto.username().get())
                .orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        if(passwordUpdateDto.newPassword().isPresent()){
            member.updatePassword(passwordEncoder, passwordUpdateDto.newPassword().get());
        }
    }
}
