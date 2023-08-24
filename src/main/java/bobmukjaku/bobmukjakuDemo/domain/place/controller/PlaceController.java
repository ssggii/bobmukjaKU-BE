package bobmukjaku.bobmukjakuDemo.domain.place.controller;

import bobmukjaku.bobmukjakuDemo.domain.place.dto.ReviewCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.ReviewDeleteDto;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.ScrapCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.place.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class PlaceController {

    /*
    *
    * <맛지도 API>
    * 리뷰 등록
    * 리뷰 삭제
    * 리뷰 조회 - uid로 리뷰 조회, 음식점 id로 리뷰 조회
    * 스크랩 등록
    * 스크랩 해제
    * 스크랩 조회 - uid로 스크랩 정보 조회, 음식점 id로 스크랩 정보 조회(uid 같이 반환)
    *
    * */

    private final PlaceService placeService;

    // 리뷰 등록
    @PostMapping("/place/review")
    public void createReview(@RequestBody ReviewCreateDto reviewCreateDto) throws Exception {
        placeService.createReview(reviewCreateDto);
    }

    // 리뷰 삭제
    @DeleteMapping("/place/review/info")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReview(@Valid @RequestBody ReviewDeleteDto reviewDeleteDto) throws Exception {
        placeService.deleteReview(reviewDeleteDto.reviewId());
    }

    // 리뷰 조회

    // 스크랩 등록
    @PostMapping("/place/scrap")
    public void createScrap(@RequestBody ScrapCreateDto scrapCreateDto) throws Exception {
        placeService.createScrap(scrapCreateDto);
    }

/*    // 스크랩 해제
    @DeleteMapping("/place/scrap/{scrapId}")
    public void deleteScrap(@PathVariable Long scrapId) throws Exception {
        placeService.deleteScrap(scrapId);
    }*/


}
