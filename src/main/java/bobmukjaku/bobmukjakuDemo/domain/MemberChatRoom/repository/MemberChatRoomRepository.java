package bobmukjaku.bobmukjakuDemo.domain.MemberChatRoom.repository;

import bobmukjaku.bobmukjakuDemo.domain.MemberChatRoom.MemberChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    Optional<MemberChatRoom> findMemberChatRoomByChatRoomAndAndJoiner(ChatRoom chatRoom, Member member);
}
