package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import bobmukjaku.bobmukjakuDemo.BaseTimeEntity;
import bobmukjaku.bobmukjakuDemo.domain.memberchatroom.MemberChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Table(name = "chatroom")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class ChatRoom extends BaseTimeEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    // member-chatroom 연관관계 매핑
    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = ALL, orphanRemoval = true)
    private List<MemberChatRoom> participants = new ArrayList<>();

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "date")
    private LocalDate meetingDate;

    @Column(name = "start_at")
    private LocalTime startTime;

    @Column(name = "end_at")
    private LocalTime endTime;

    @Column(name = "kind_of_food")
    private String kindOfFood;

    @Column(name = "total")
    private int total;

    @Column(name = "current_num")
    private int currentNum;

    /* 연관관계 메서드 */
    // 참여자 추가
    public void addParticipant(MemberChatRoom memberChatRoom) {
        participants.add(memberChatRoom);
        addCurrentNum(); // 해당 방의 참여자 추가 시 현재 인원++
    }

    // 참여자 삭제
    public void deleteParticipant(MemberChatRoom memberChatRoom) {
        Member member = memberChatRoom.getJoiner();
        participants.removeIf(memberChatRoom1 -> memberChatRoom1.getJoiner().equals(member));
        currentNum--;
    }

    // 중복 참여자 확인
    public boolean isParticipant(Member member) {
        return participants.stream()
                .map(MemberChatRoom::getJoiner)
                .anyMatch(joiner -> joiner.equals(member));
    }

    // 참여 인원 증가
    public void addCurrentNum(){
        this.currentNum++;
    }

}
