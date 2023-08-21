package bobmukjaku.bobmukjakuDemo.domain.place.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.ScrapCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.place.service.PlaceService;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 이미지 다운로드 (미완)
    @GetMapping(value = "/{imagePath}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String imagePath) {
        byte[] imageBytes = placeService.downloadImage(imagePath);

        if (imageBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageBytes.length);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
