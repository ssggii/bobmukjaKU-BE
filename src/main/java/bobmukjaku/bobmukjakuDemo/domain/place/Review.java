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
    private String placeName; // 음식점 이름

    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents; // 리뷰 내용

    @Column(name = "image_Url")
    private String imageUrl; // 리뷰 사진 링크

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid", nullable = true)
    private Member writer; // 작성자

    public ReviewInfoDto toDto(Review review){
        if(review.writer == null){
            return ReviewInfoDto.builder().uid(-1L).placeId(placeId).placeName(placeName).contents(contents).imageUrl(imageUrl).build();
        } else {
            return ReviewInfoDto.builder().uid(writer.getUid()).placeId(placeId).placeName(placeName).contents(contents).imageUrl(imageUrl).build();
        }
    }

    public void setWriter(Member member) {
        this.writer = member;
    }
}
