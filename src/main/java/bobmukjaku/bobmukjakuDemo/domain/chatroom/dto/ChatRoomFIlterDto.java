package bobmukjaku.bobmukjakuDemo.domain.chatroom.dto;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;

import java.util.List;
import java.util.Optional;

public record ChatRoomFIlterDto(List<ChatRoom> filteredChatRooms,
                                String nextFilter,
                                String input) {


}
