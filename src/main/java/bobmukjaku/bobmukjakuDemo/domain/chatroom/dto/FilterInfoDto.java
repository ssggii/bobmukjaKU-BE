package bobmukjaku.bobmukjakuDemo.domain.chatroom.dto;

import java.util.Optional;

public record FilterInfoDto(Optional<String> filterType, Optional<String> filterValue) {

}
