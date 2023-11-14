package bobmukjaku.bobmukjakuDemo.domain.member.controller;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.*;
import bobmukjaku.bobmukjakuDemo.domain.member.service.MemberService;
import bobmukjaku.bobmukjakuDemo.global.login.handler.LogoutService;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    /*
     *
     * <회원 API>
     * 로그아웃
     * 메일인증
     * 회원가입
     * 회원조회 - 닉네임 중복 검사, 전체 조회, uid로 조회, 내 정보 조회, 시간표 조회
     * 회원수정 - 기본 정보 수정, 메일 인증 후 비밀번호 재설정, 시간표 저장
     * 회원탈퇴
     *
     * */

    private final MemberService memberService;
    private final LogoutService logoutService;

    /*@PostMapping("/auth/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        logoutService.logout(request);
        return ResponseEntity.ok("로그아웃 완료");
    }*/

    // 회원가입
    @PostMapping("/signUp")
    @ResponseStatus(HttpStatus.OK)
    public void signUp(@Valid @RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
        memberService.signUp(memberSignUpDto);
    }

    // 닉네임 중복 검사
    @GetMapping("/check/nickname")
    public ResponseEntity<Boolean> checkNicknameDoubled(@RequestBody String nickName) throws Exception {
        return ResponseEntity.ok(memberService.checkNickName(nickName));
    }

    // 전체 회원 조회
    @GetMapping("/members/info")
    public ResponseEntity getAllMembers() throws Exception {
        List<MemberInfoDto> allMembers = memberService.getAllMembers();
        return new ResponseEntity(allMembers, HttpStatus.OK);
    }

    // uid로 회원정보 조회
    @GetMapping("/member/info/{uid}")
    public ResponseEntity getInfo(@Valid @PathVariable("uid") Long id) throws Exception{
        MemberInfoDto info = memberService.getInfo(id);
        return new ResponseEntity(info, HttpStatus.OK);
    }

    // 내 정보 조회
    @GetMapping("/member/info")
    public ResponseEntity getMyInfo(HttpServletResponse response) throws Exception {
        MemberInfoDto info = memberService.getMyInfo();
        return new ResponseEntity(info, HttpStatus.OK);
    }

    // 회원정보 수정
    @PutMapping("/member/info")
    @ResponseStatus(HttpStatus.OK)
    public void updateInfo(@Valid @RequestBody MemberUpdateDto memberUpdateDto) throws Exception {
        memberService.updateMemberInfo(memberUpdateDto, SecurityUtil.getLoginUsername());
    }

    // 회원탈퇴
    @DeleteMapping("/member/account")
    @ResponseStatus(HttpStatus.OK)
    public void withdrawMember(HttpServletResponse response) throws Exception {
        memberService.withdrawMember(SecurityUtil.getLoginUsername());
    }

    // 메일인증
    @GetMapping("/mailAuth")
    @ResponseBody
    public HashedAuthCodeDto mailAuth(@RequestParam String email) throws Exception {
        return memberService.mailAuth(email);
    }

    // 시간표 저장
    @PostMapping("/member/info/timeTable")
    @ResponseStatus(HttpStatus.OK)
    public void saveTimeBlock(@RequestBody List<TimeBlockDto> timeBlockDtoList) throws Exception {
        memberService.updateTimeBlock(timeBlockDtoList);
    }

    // 시간표 조회
    @GetMapping("/timeTable")
    public ResponseEntity getMyTimeTable(HttpServletResponse response) throws Exception {
        return new ResponseEntity(memberService.getMyTimeBlocks(), HttpStatus.OK);
    }

    // rate 업데이트
    @PutMapping("/member/info/rate")
    @ResponseStatus(HttpStatus.OK)
    public void rateUpdate(@RequestBody RateUpdateDto rateUpdateDto) throws Exception {
        System.out.println("\n\n\n" + rateUpdateDto.uid() + "\n" + rateUpdateDto.score() + "\n\n");
        memberService.rateUpdate(rateUpdateDto.uid(), rateUpdateDto.score());
    }

    // (메일 인증 후) 비밀번호 재설정
    @PutMapping("/resetPassword")
    @ResponseStatus(HttpStatus.OK)
    public void resetMemberPassword(@RequestBody PasswordUpdateDto passwordUpdateDto, HttpServletResponse response) throws Exception {
        memberService.resetMemberPassword(passwordUpdateDto);
    }

    //로그인 없이 uid로 닉네임,rate,배경색 검색
    @GetMapping("/member/name_rate_bg/{uid}")
    public NameRateBgDto getNameRateBg(@PathVariable("uid") Long uid){
        return memberService.getNameRateBg(uid);
    }
}
