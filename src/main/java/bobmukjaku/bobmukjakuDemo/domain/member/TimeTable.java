package bobmukjaku.bobmukjakuDemo.domain.member;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "timetable")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class TimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK
    private Member member;

    @Column(name = "index")
    private List<String> availableTime;
}
