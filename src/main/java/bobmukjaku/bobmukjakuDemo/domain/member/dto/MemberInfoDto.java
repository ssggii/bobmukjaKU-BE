package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MemberInfoDto {

    private Long uid; // uid
    private String memberEmail; // 이메일
    private String memberNickName; // 닉네임
    private LocalDate certificatedAt; // 인증 날짜
    private int rate; // 평가 점수
    private String profileColor; // 프로필 배경색

    @Builder
    public MemberInfoDto(Member member){
        this.uid = member.getUid();
        this.memberEmail = member.getMemberEmail();
        this.memberNickName = member.getMemberNickName();
        this.certificatedAt = member.getCertificatedAt();
        this.rate = member.getRate();
        this.profileColor = member.getProfileColor();
    }
}
