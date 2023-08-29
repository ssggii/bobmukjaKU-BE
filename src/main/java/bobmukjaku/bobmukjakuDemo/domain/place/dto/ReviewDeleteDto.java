package bobmukjaku.bobmukjakuDemo.domain.place.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewDeleteDto(Long uid, String placeId) {
}
