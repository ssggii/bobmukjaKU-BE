package bobmukjaku.bobmukjakuDemo.domain.memberchatroom.repository;

import bobmukjaku.bobmukjakuDemo.domain.memberchatroom.MemberChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    Optional<MemberChatRoom> findMemberChatRoomByChatRoomAndAndJoiner(ChatRoom chatRoom, Member member);
}
