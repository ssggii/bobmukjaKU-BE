package bobmukjaku.bobmukjakuDemo.domain.member.service;

import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberUpdateDto;

public interface MemberService {

    // 회원가입, 회원정보수정, 회원탈퇴, 회원정보조회
    public void signUp(MemberSignUpDto memberSignUpDto) throws Exception;
    public void updateMemberInfo(MemberUpdateDto memberUpdateDto) throws Exception;
    public void updatePassword(String checkPassword, String toBePassword) throws Exception;
    public void withdraw(String checkPassword) throws Exception;

    MemberInfoDto getInfo(Long id) throws Exception; // id로 조회
    MemberInfoDto getMyInfo() throws Exception; // 이메일로 조회
}
