package bobmukjaku.bobmukjakuDemo.domain.member.controller;

import bobmukjaku.bobmukjakuDemo.domain.member.dto.*;
import bobmukjaku.bobmukjakuDemo.domain.member.service.MemberService;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
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

    private final MemberService memberService;

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

    // 회원정보 수정
    @PutMapping("/member/info")
    @ResponseStatus(HttpStatus.OK)
    public void updateInfo(@Valid @RequestBody MemberUpdateDto memberUpdateDto) throws Exception {
        memberService.updateMemberInfo(memberUpdateDto, SecurityUtil.getLoginUsername());
    }

    // 비밀번호 재설정
    @PutMapping("/member/info/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) throws Exception {
        memberService.updatePassword(updatePasswordDto.checkPassword(), updatePasswordDto.toBePassword(), SecurityUtil.getLoginUsername());
    }

    // 회원탈퇴
    @DeleteMapping("/member/info")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@Valid @RequestBody MemberWithdrawDto memberWithdrawDto) throws Exception {
        memberService.withdraw(memberWithdrawDto.checkPassword(), SecurityUtil.getLoginUsername());
    }

    // 전체 회원 조회
    @GetMapping("/members")
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
}
