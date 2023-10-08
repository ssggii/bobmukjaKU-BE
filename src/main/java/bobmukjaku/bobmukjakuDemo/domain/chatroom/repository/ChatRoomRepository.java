package bobmukjaku.bobmukjakuDemo.domain.chatroom.repository;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, JpaSpecificationExecutor<ChatRoom> {

    List<ChatRoom> findAllByOrderByCreatedAtDesc(); // 최신 순 정렬

    Optional<ChatRoom> findChatRoomByRoomName(String roomName); // 모집방 이름으로 검색

}
