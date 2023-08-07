package bobmukjaku.bobmukjakuDemo.domain.chatroom.repository;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
