package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import javax.swing.text.html.Option;
import java.util.Optional;

public record MemberUpdateDto(Optional<String> nickName,
                              Optional<String> profileColor,
                              Optional<String> certificatedAt,
                              Optional<Integer> rate,
                              Optional<String> toBePassword) {
}
