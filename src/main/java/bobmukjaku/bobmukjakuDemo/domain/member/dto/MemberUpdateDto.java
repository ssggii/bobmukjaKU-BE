package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import java.util.Optional;

public record MemberUpdateDto(Optional<String> nickName,
                              Optional<String> profileColor,
                              Optional<String> certificatedAt,
                              Optional<Integer> rate) {
}
