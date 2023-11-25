package bobmukjaku.bobmukjakuDemo.domain.place;

import jakarta.persistence.*;
import lombok.*;


@Table(name = "restaurant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Place {

    @Id
    private String placeId; // 음식점 ID

    @Column(name = "place_name")
    private String placeName; // 음식점 이름

    @Builder.Default
    @Column(name = "scrap_count")
    private int scrapCount = 0; // 스크랩 수

    @Builder.Default
    @Column(name = "review_count")
    private int reviewCount = 0; // 리뷰 수

    public void addScrapCount(){
        this.scrapCount++;
    }
    public void subScrapCount(){
        this.scrapCount--;
    }
    public void addReviewCount(){
        this.reviewCount++;
    }
    public void subReviewCount(){
        this.reviewCount--;
    }

}
