package bobmukjaku.bobmukjakuDemo.domain.place;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.ReviewInfoDto;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId; // PK

    @Column(name = "place_id")
    private String placeId; // 음식점 id

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents; // 리뷰 내용

    @Column(name = "image_Url")
    private String imageUrl; // 리뷰 사진 링크

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private Member writer; // 작성자

    public ReviewInfoDto toDto(Review review){
        return ReviewInfoDto.builder().contents(review.contents).imageUrl(imageUrl).build();
    }

}
