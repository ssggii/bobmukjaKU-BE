package bobmukjaku.bobmukjakuDemo.domain.chatroom.repository;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findChatRoomsByKindOfFood(String kindOfFood); // 음식 종류로 모집방 검색

    List<ChatRoom> findChatRoomsByTotal(int total); // 정원 수로 모집방 검색

}
