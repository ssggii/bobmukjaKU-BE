package bobmukjaku.bobmukjakuDemo.domain.chatroom.dto;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@NoArgsConstructor
public class FilteredRoomInfoDto {

    private Long roomId; // 모집방 id
    private String roomName; // 방 이름
    private LocalDate meetingDate; // 모임 날짜
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private String kindOfFood; // 음식 분류
    private int total; // 전체 인원
    private int currentNum; // 참여 인원
    private int hasFriend; // 친구 참여(1), 친구 참여X(0)

    @Builder
    public FilteredRoomInfoDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getChatRoomId();
        this.roomName = chatRoom.getRoomName();
        this.meetingDate = chatRoom.getMeetingDate();
        this.startTime = chatRoom.getStartTime();
        this.endTime = chatRoom.getEndTime();
        this.kindOfFood = chatRoom.getKindOfFood();
        this.total = chatRoom.getTotal();
        this.currentNum = chatRoom.getCurrentNum();
        this.hasFriend = 0; // 친구 참여X (디폴트)
    }

    public void updateHasFriend(int hasFriend){
        this.hasFriend = hasFriend;
    }
}
