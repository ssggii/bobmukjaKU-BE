package bobmukjaku.bobmukjakuDemo.domain.chatroom.dto;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;

import java.time.LocalDate;
import java.time.LocalTime;

public record ChatRoomCreateDto(String roomName, String date,
                                String startTime, String endTime, String kindOfFood, int total) {

    public ChatRoom toEntity(){
        ChatRoom chatRoom = ChatRoom.builder().roomName(roomName)
                .meetingDate(LocalDate.parse(date))
                .startTime(LocalTime.parse(startTime))
                .endTime(LocalTime.parse(endTime))
                .kindOfFood(kindOfFood)
                .total(total)
                .build();
        return chatRoom;
    }
}