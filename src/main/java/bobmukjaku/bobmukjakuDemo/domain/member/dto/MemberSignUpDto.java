package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import jakarta.persistence.PrePersist;

import java.time.LocalDate;

public record MemberSignUpDto(String memberEmail, String password, String memberNickname, int rate, String profileColor, LocalDate certificatedAt) {

    public Member toEntity(){
        return Member.builder()
                .memberEmail(memberEmail)
                .memberPassword(password)
                .memberNickName(memberNickname)
                .rate(rate)
                .profileColor(profileColor)
                .certificatedAt(certificatedAt)
                .build();
    }

}
