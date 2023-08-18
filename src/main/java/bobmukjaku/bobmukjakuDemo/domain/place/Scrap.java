package bobmukjaku.bobmukjakuDemo.domain.place;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Table(name = "scrap")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scrapId; // PK

    @Column(name = "place_id")
    private String placeId; // 음식점 ID

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private Member member; // 스크랩한 회원

}
