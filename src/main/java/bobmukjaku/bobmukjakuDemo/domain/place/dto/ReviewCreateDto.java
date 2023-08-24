package bobmukjaku.bobmukjakuDemo.domain.place.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.place.Review;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewCreateDto {

    private String placeId;
    @Column(columnDefinition = "TEXT")
    private String contents;
    private String imageUrl;
    private Long uid;

    public Review toEntity(Member member) {
        return Review.builder()
                .placeId(placeId)
                .contents(contents)
                .imageUrl(imageUrl)
                .writer(member)
                .build();
    }

}
