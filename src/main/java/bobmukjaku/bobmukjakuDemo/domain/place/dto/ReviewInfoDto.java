package bobmukjaku.bobmukjakuDemo.domain.place.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewInfoDto {

    @Column(columnDefinition = "TEXT")
    private String contents; // 리뷰 내용

    private String imageUrl; // 이미지 링크

    /*
    * 필요에 맞춰 수정 예정
    * */
}
