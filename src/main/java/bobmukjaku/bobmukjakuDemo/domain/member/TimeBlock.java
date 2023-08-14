package bobmukjaku.bobmukjakuDemo.domain.member;

import bobmukjaku.bobmukjakuDemo.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import static jakarta.persistence.FetchType.LAZY;

@Table(name = "time_table")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TimeBlock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dayOfWeek")
    private Integer dayOfWeek; // 요일

    @Column(name = "time")
    private LocalTime time; // 시간

    // member-timeslot 연관관계 매핑
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private Member member;

    public TimeBlock(int dayOfWeek, LocalTime time){
        this.dayOfWeek = dayOfWeek;
        this.time = time;
    }

}
