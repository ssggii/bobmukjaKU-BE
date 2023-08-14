package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import java.time.LocalTime;

public record TimeBlockCreateDto(Integer dayOfWeek, LocalTime time) {
}
