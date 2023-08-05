package bobmukjaku.bobmukjakuDemo.domain.member;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne
    @JoinColumn(name = "member_id") // FK
    private Member member;
}
