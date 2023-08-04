package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import bobmukjaku.bobmukjakuDemo.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Table(name = "chatroom")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class ChatRoom extends BaseTimeEntity {

    @Id
    private String id; // PK

    @Column(name = "room_name", nullable = false) // 길이 추가 예정
    private String roomName;

    @Column(name = "date")
    private LocalDate meetingDate;

    @Column(name = "start_at")
    private LocalTime startTime;

    @Column(name = "end_at")
    private LocalTime endTime;

    @Column(name = "kind_of_food")
    private String kindOfFood;

    @Column(name = "member_num")
    private Integer memberNum;

}
