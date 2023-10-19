package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import javax.swing.text.html.Option;
import java.util.Optional;

public record PasswordUpdateDto(Optional<String> username, Optional<String> newPassword) {
}
