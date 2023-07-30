package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;

public record MemberSignUpDto(String memberEmail, String password, String memberNickname) {

    public Member toEntity(){
        return Member.builder()
                .memberEmail(memberEmail)
                .memberPassword(password)
                .memberNickName(memberNickname)
                .build();
    }
}
