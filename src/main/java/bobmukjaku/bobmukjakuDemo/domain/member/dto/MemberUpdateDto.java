package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import java.time.LocalDate;
import java.util.Optional;

public record MemberUpdateDto(Optional<String> nickName, Optional<String> profileColor, Optional<LocalDate> certificatedAt) {
}
