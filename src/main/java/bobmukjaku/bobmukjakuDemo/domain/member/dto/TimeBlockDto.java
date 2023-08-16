package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.TimeBlock;

import java.time.LocalTime;

public record TimeBlockDto(Integer dayOfWeek, String time) { // 시간은 00:00~23:59 사이의 값이어야 함

    public TimeBlock toEntity(Member member){
        TimeBlock timeBlock = TimeBlock.builder().dayOfWeek(dayOfWeek).time(LocalTime.parse(time)).member(member).build();
        return timeBlock;
    }
}
