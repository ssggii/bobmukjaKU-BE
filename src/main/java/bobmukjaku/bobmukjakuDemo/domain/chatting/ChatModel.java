package bobmukjaku.bobmukjakuDemo.domain.chatting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatModel {

    private String message;
    private String senderUid;
    private String senderName;
    private Long time;
    private Boolean isShareMessage;

    private String chatRoomId;
    private Boolean isProfanity;
}
