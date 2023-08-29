package bobmukjaku.bobmukjakuDemo.domain.place;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.ScrapInfoDto;
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

    @Column(name = "place_name")
    private String placeName; // 음식점 이름

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private Member member; // 스크랩한 회원

    public ScrapInfoDto toDto(Scrap scrap) {
        return new ScrapInfoDto(member.getUid(), placeName, placeId);
    }

}
