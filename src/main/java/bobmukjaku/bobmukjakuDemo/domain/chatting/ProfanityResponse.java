package bobmukjaku.bobmukjakuDemo.domain.chatting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfanityResponse {
    private Boolean profanity;
    private Float probability;
    private String input;
}
