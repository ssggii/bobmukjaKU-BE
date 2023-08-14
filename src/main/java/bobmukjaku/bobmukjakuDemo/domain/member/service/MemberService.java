package bobmukjaku.bobmukjakuDemo.domain.member.service;

import bobmukjaku.bobmukjakuDemo.domain.chatting.ChatModel;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.*;

import java.util.List;

public interface MemberService {

    public void signUp(MemberSignUpDto memberSignUpDto) throws Exception; // 회원가입
    public void updateMemberInfo(MemberUpdateDto memberUpdateDto, String username) throws Exception; // 회원정보수정
    public void withdraw(String checkPassword, String username) throws Exception; // 회원탈퇴
    public boolean checkNickName(String nickName) throws Exception; // 닉네임 중복 검사

    MemberInfoDto getInfo(Long id) throws Exception; // id로 조회
    MemberInfoDto getMyInfo() throws Exception; // 이메일로 조회
    List<MemberInfoDto> getAllMembers() throws Exception; // 전체 회원 조회
    public HashedAuthCodeDto mailAuth(String email) throws Exception; // 메일 인증
    public void saveTimeBlock(List<TimeBlockCreateDto> timeBlockCreateDtoList); // 시간표 저장

}
