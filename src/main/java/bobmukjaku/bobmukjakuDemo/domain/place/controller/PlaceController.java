package bobmukjaku.bobmukjakuDemo.domain.place.controller;

import bobmukjaku.bobmukjakuDemo.domain.place.dto.*;
import bobmukjaku.bobmukjakuDemo.domain.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
    public void createReview(@RequestBody ReviewInfoDto reviewInfoDto) throws Exception {
        placeService.createReview(reviewInfoDto);
    }

    // 리뷰 삭제
    @PostMapping("/place/review/info")
    public void deleteReview(@RequestBody ReviewDeleteDto reviewDeleteDto) throws Exception {
        placeService.deleteReview(reviewDeleteDto);
    }

    // uid로 리뷰 조회
    @GetMapping("/place/review/info/1/{uid}")
    public ResponseEntity getReviewInfoByUid(@PathVariable Long uid) throws Exception {
        List<ReviewInfoDto> result = placeService.getReviewInfoByUid(uid);
        if(result != null && !result.isEmpty())
            return new ResponseEntity(result, HttpStatus.OK);
        else
            return new ResponseEntity("등록된 리뷰가 없습니다", HttpStatus.NO_CONTENT);
    }

    // 음식점 id로 리뷰 조회
    @GetMapping("/place/review/info/2/{placeId}")
    public ResponseEntity getReviewInfoByPlaceId(@PathVariable String placeId) throws Exception {
        List<ReviewInfoDto> result = placeService.getReviewInfoByPlaceId(placeId);
        if(result != null)
            return new ResponseEntity(result, HttpStatus.OK);
        else
            return new ResponseEntity("리뷰가 없습니다.", HttpStatus.NO_CONTENT);
    }

    // 스크랩 등록
    @PostMapping("/place/scrap")
    public void createScrap(@RequestBody ScrapInfoDto scrapInfoDto) throws Exception {
        placeService.createScrap(scrapInfoDto);
    }

    // 스크랩 해제
    @PostMapping("/place/scrap/remove")
    public void removeScrap(@RequestBody ScrapInfoDto scrapInfoDto) throws Exception {
        placeService.deleteScrap(scrapInfoDto);
    }

    // uid로 스크랩 조회
    @GetMapping("/place/scrap/info/1/{uid}")
    public ResponseEntity getScrapInfoByUid(@PathVariable Long uid) throws Exception {
        List<ScrapInfoDto> result = placeService.getScrapInfoByUid(uid);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    // 음식점 id로 스크랩 조회
    @GetMapping("/place/scrap/info/2/{placeId}")
    public ResponseEntity getScrapInfoByPlaceId(@PathVariable String placeId) throws Exception {
        List<ScrapInfoDto> result = placeService.getScrapInfoByPlaceId(placeId);
        if(result != null)
            return new ResponseEntity(result, HttpStatus.OK);
        else
            return new ResponseEntity<>("해당 음식점 정보가 없습니다.", HttpStatus.NOT_FOUND); // 음식점 id가 없어진 경우
    }

    // 음식점 스크랩 수 조회
    @GetMapping("/place/scrap/count/{placeId}")
    public ResponseEntity<Long> getScrapCountsOfPlace(@PathVariable String placeId) throws Exception {
        return new ResponseEntity<>(placeService.getScrapCountsOfPlace(placeId), HttpStatus.OK);
    }

    // 음식점 데이터 DB에 저장
    @GetMapping("/api")
    public ResponseEntity callApi() throws Exception{
        StringBuilder result = new StringBuilder();
        String urlStr = "https://apis.data.go.kr/B553077/api/open/sdsc2/storeListInDong?" +
                "ServiceKey=VYIyj%2BcymAgnf1xMSx%2Bx0aCOwrLNT4bFmRhEc2BCLNOHi772zuCMomRy5AvxiLp6CSXadCFA70HEePy6Ewf2%2Fg%3D%3D" +
                "&pageNo=0" +
                "&numOfRows=1000" +
                "&divId=adongCd" +
                "&key=11215710" +
                "&indsLclsCd=I2" +
                "&indsMclsCd=I201" +
                "&indsSclsCd=I20101" +
                "&type=json";
        URL url = new URL(urlStr);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

        String returnLine;
        while((returnLine = bufferedReader.readLine()) != null){
            result.append(returnLine + "\n\r");
        }

        urlConnection.disconnect();
        int rowNum = placeService.loadSave(result.toString()); // DB에 저장된 데이터 개수

        return new ResponseEntity<>("저장된 데이터 개수: " + rowNum, HttpStatus.OK);
    }

}
