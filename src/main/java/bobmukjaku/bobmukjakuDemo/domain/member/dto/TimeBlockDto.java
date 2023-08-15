package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.TimeBlock;

import java.time.LocalTime;

public record TimeBlockDto(Integer dayOfWeek, String time) { // 시간은 00:00~23:59 사이의 값이어야 함

    public TimeBlock toEntity(TimeBlockDto timeBlockDto){
        TimeBlock timeBlock = TimeBlock.builder().dayOfWeek(timeBlockDto.dayOfWeek).time(LocalTime.parse(timeBlockDto.time)).build();
        return timeBlock;
    }
}
