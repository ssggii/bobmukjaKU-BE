package bobmukjaku.bobmukjakuDemo.domain.MemberChatRoom;

import bobmukjaku.bobmukjakuDemo.BaseTimeEntity;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "member_chatroom")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
public class MemberChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_chatroom_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member joiner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    public MemberChatRoom(Member member, ChatRoom chatRoom) {
        this.joiner = member;
        this.chatRoom = chatRoom;
        // 양방향 연관관계 설정
        joiner.getJoiningRooms().add(this);
        chatRoom.getParticipants().add(this);
    }

}
