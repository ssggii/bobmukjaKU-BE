package bobmukjaku.bobmukjakuDemo.domain.member;

import bobmukjaku.bobmukjakuDemo.BaseTimeEntity;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.TimeBlockDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

import static jakarta.persistence.FetchType.LAZY;

@Table(name = "time_table")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class TimeBlock {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long TimeBlockId;

    @Column(name = "dayOfWeek")
    private Integer dayOfWeek; // 요일

    @Column(name = "time")
    private LocalTime time; // 시간

    // member-timeslot 연관관계 매핑
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private Member member;

    public TimeBlockDto toDto(TimeBlock timeBlock){
        TimeBlockDto dto = new TimeBlockDto(timeBlock.getDayOfWeek(), timeBlock.getTime().toString());
        return dto;
    }

}
