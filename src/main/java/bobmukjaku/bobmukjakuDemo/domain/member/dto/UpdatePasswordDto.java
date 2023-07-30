package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordDto (@NotBlank(message = "비밀번호를 입력해주세요") String checkPassword,
                                 @NotBlank(message = "비밀번호를 입력해주세요")
                                 @Pattern(regexp = "^(?=.*[!@^&,.?])[A-Za-z\\d@$!%*#?&]{8,15}$",
                                         message = "비밀번호는 8~15 자리이면서 특수문자(@$!%*#?&)를 포함해야합니다.")
                                 String toBePassword){


}
