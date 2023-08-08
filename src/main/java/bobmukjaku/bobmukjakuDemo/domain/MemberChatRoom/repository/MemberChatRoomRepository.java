package bobmukjaku.bobmukjakuDemo.domain.MemberChatRoom.repository;

import bobmukjaku.bobmukjakuDemo.domain.MemberChatRoom.MemberChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
}
