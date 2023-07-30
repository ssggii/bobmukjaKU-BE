package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MemberSignUpDto(@NotBlank(message = "이메일을 입력해주세요") @Email String memberEmail,
                              @NotBlank(message = "비밀번호를 입력해주세요")
                              @Pattern(regexp = "^(?=.*[!@^&,.?])[A-Za-z\\d@$!%*#?&]{8,15}$",
                                      message = "비밀번호는 8~15 자리이면서 특수문자(@$!%*#?&)를 포함해야합니다.")
                              String password,
                              @NotBlank(message = "닉네임을 입력해주세요.")
                              @Size(min = 1, max = 8, message = "1~8자 이내로 설정해주세요")
                              String memberNickname,
                              int rate, String profileColor, LocalDate certificatedAt) {

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
