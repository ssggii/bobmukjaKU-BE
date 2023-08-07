package bobmukjaku.bobmukjakuDemo.domain.member.service;

import bobmukjaku.bobmukjakuDemo.chatting.ChatModel;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.HashedAuthCodeDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberUpdateDto;

import java.util.List;

public interface MemberService {

    // 회원가입, 회원정보수정, 회원탈퇴, 회원정보조회
    public void signUp(MemberSignUpDto memberSignUpDto) throws Exception;
    public void updateMemberInfo(MemberUpdateDto memberUpdateDto, String username) throws Exception;
    public void updatePassword(String checkPassword, String toBePassword, String username) throws Exception;
    public void withdraw(String checkPassword, String username) throws Exception;
    public boolean checkNickName(String nickName) throws Exception;

    MemberInfoDto getInfo(Long id) throws Exception; // id로 조회
    MemberInfoDto getMyInfo() throws Exception; // 이메일로 조회
    List<MemberInfoDto> getAllMembers() throws Exception; // 전체 회원 조회
    public HashedAuthCodeDto mailAuth(String email) throws Exception; //메일인증
    public void sendMessageToFireBase(ChatModel md) throws Exception; //파이어베이스로 메시지 전송
}
