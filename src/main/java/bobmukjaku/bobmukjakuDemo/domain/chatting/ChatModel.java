package bobmukjaku.bobmukjakuDemo.domain.chatting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ChatModel {

    private String message;
    private String senderUid;
    private String senderName;
    private Long time;
    private Boolean shareMessage;
    private String chatRoomId;
    private Boolean profanity;
    private Map<String,Boolean> readList;
}
