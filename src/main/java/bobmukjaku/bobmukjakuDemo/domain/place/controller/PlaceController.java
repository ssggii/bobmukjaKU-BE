package bobmukjaku.bobmukjakuDemo.domain.place.controller;

import bobmukjaku.bobmukjakuDemo.domain.place.dto.ReviewCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.ScrapCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    * 리뷰 조회 - uid로 리뷰 조회, 음식점 id로 리뷰 조회
    * 리뷰 삭제
    * 스크랩 등록
    * 스크랩 해제
    * 스크랩 조회 - uid로 스크랩 정보 조회, 음식점 id로 스크랩 정보 조회(uid 같이 반환)
    *
    * */

    private final PlaceService placeService;

    // 이미지 storage에 업로드
    @PostMapping("/files")
    public String uploadFile(@RequestParam("file") MultipartFile file, String fileName) throws Exception {
        if(file.isEmpty()) return "빈 파일입니다";
        return placeService.uploadFile(file, fileName);
    }

/*    // 이미지 다운로드
    @GetMapping(value = "/{imagePath}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String imagePath) {
        byte[] imageBytes = placeService.downloadFile(imagePath);

        if (imageBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageBytes.length);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }*/

    // 리뷰 등록
    @PostMapping("/place/review")
    public void createReview(@RequestBody ReviewCreateDto reviewCreateDto) throws Exception {
        placeService.createReview(reviewCreateDto);
    }

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
