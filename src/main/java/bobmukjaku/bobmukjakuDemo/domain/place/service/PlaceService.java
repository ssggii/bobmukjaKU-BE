package bobmukjaku.bobmukjakuDemo.domain.place.service;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.Place;
import bobmukjaku.bobmukjakuDemo.domain.place.Review;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.*;
import bobmukjaku.bobmukjakuDemo.domain.place.repository.PlaceRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.repository.ReviewRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.repository.ScrapRepository;
import com.google.cloud.storage.*;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {

    @Value("${app.firebase-bucket}")
    private String fireBaseBucket;

    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final ScrapRepository scrapRepository;

    // 이미지 url 생성
    public String getImageUrl(String imageFileName) throws Exception, FirebaseAuthException {
        try {
            Storage storage = StorageOptions.getDefaultInstance().getService(); // Firebase Storage 클라이언트 초기화
            BlobId blobId = BlobId.of(fireBaseBucket, imageFileName); // 이미지의 BlobId 생성
            Blob blob = storage.get(blobId); // Blob 가져오기
            String imageUrl = blob.getMediaLink(); // 이미지의 다운로드 URL 가져오기
            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 리뷰 등록
    public void createReview(ReviewInfoDto reviewInfoDto) throws Exception {
        Member member = memberRepository.findById(reviewInfoDto.getUid()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Review review = reviewInfoDto.toEntity(member);
        member.addReview(review);

        Place place = placeRepository.findByPlaceId(reviewInfoDto.getPlaceId())
                .orElseThrow(() -> new Exception("Place not found with ID: " + reviewInfoDto.getPlaceId()));
        place.addReviewCount();
    }

    // 리뷰 삭제
    public void deleteReview(ReviewDeleteDto reviewDeleteDto) throws Exception {
        Member member = memberRepository.findById(reviewDeleteDto.uid()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        member.getReviewList().stream()
                .filter(review -> review.getPlaceId().equals(reviewDeleteDto.placeId()))
                .findFirst()
                .ifPresent(review -> {
                    review.getWriter().deleteReview(review);
                    reviewRepository.delete(review);
                });

        Place place = placeRepository.findByPlaceId(reviewDeleteDto.placeId())
                .orElseThrow(() -> new Exception("Place not found with ID: " + reviewDeleteDto.placeId()));
        place.subReviewCount();
    }

    // uid로 리뷰 조회
    public List<ReviewInfoDto> getReviewInfoByUid(Long uid) throws Exception {
        Member member = memberRepository.findById(uid).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        return member.getReviewList().stream().map(review -> review.toDto(review)).collect(Collectors.toList());
    }

    // 음식점 id로 리뷰 조회
    public List<ReviewInfoDto> getReviewInfoByPlaceId(String placeId) throws Exception {
        List<Review> reviewList = reviewRepository.findAllByPlaceId(placeId);
        if(reviewList != null && !reviewList.isEmpty()){
            return reviewList.stream().map(review -> review.toDto(review)).collect(Collectors.toList());
        }
        return null;
    }

    // 스크랩 등록
    public void createScrap(ScrapInfoDto scrapInfoDto) throws Exception {
        Member member = memberRepository.findById(scrapInfoDto.uid()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Scrap scrap = scrapInfoDto.toEntity(member);
        member.addScrap(scrap);

        Place place = placeRepository.findByPlaceId(scrapInfoDto.placeId())
                .orElseThrow(() -> new Exception("Place not found with ID: " + scrapInfoDto.placeId()));
        place.addScrapCount();
    }

    // 스크랩 해제
    public void deleteScrap(ScrapInfoDto scrapInfoDto) throws Exception {
        Member member = memberRepository.findById(scrapInfoDto.uid()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        member.getScrapList().stream()
                .filter(scrap -> scrap.getPlaceId().equals(scrapInfoDto.placeId()))
                .findFirst()
                .ifPresent(scrap -> {
                    scrap.getMember().deleteScrap(scrap);
                    scrapRepository.delete(scrap);
                });

        Place place = placeRepository.findByPlaceId(scrapInfoDto.placeId())
                .orElseThrow(() -> new Exception("Place not found with ID: " + scrapInfoDto.placeId()));
        place.subScrapCount();
    }

    // uid로 스크랩 조회
    public List<ScrapInfoDto> getScrapInfoByUid(Long uid) throws Exception {
        Member member = memberRepository.findById(uid).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        return member.getScrapList().stream().map(scrap -> scrap.toDto(scrap)).collect(Collectors.toList());
    }

    // 음식점 id로 스크랩 조회
    public List<ScrapInfoDto> getScrapInfoByPlaceId(String placeId) throws Exception {
        List<Scrap> scrapList = scrapRepository.findAllByPlaceId(placeId);
        if(scrapList != null && !scrapList.isEmpty())
            return scrapRepository.findAllByPlaceId(placeId).stream().map(scrap -> scrap.toDto(scrap)).collect(Collectors.toList());
        else
            return null;
    }

    // 음식점 스크랩 수 조회
    public Long getScrapCountsOfPlace(String placeId) throws Exception {
        return scrapRepository.countByPlaceId(placeId);
    }

    // 상위 스크랩 음식점 조회
    public List<TopScrapRestaurantsInterface> getTopScrapRestaurants() {
        return placeRepository.findTop10CustomSort();
    }

    // 키워드를 음식점 이름으로 포함하는 음식점 조회
    public List<PlaceInfoDto> getPlacesByKeyword(String keyword) throws Exception {
        List<Place> places = placeRepository.findByPlaceNameIgnoreCase(keyword);
        return places.stream().map(Place::toDto).collect(Collectors.toList());
    }

}
