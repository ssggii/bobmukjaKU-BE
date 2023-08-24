package bobmukjaku.bobmukjakuDemo.domain.place.service;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.Review;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.ReviewCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.ScrapCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.place.repository.ReviewRepository;
import com.google.cloud.storage.*;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {

    @Value("${app.firebase-bucket}")
    private String fireBaseBucket;

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    // 이미지 업로드
    public String uploadFile(MultipartFile file, String fileName) throws Exception, FirebaseAuthException {
        Bucket bucket = StorageClient.getInstance().bucket(fireBaseBucket);
        InputStream content = new ByteArrayInputStream(file.getBytes());
        Blob blob = bucket.create(fileName.toString(), content, file.getContentType());
        return blob.getMediaLink();
    }

    // 이미지 다운로드
    public byte[] downloadFile(String imagePath) {
        try {
            // Firebase Storage 클라이언트 초기화
            Storage storage = StorageOptions.getDefaultInstance().getService();

            // 이미지의 BlobId 생성
            BlobId blobId = BlobId.of(fireBaseBucket, imagePath);

            // Blob 가져오기
            Blob blob = storage.get(blobId);

            // Blob을 바이트 배열로 읽기
            byte[] content = blob.getContent();

            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 리뷰 등록
    public void createReview(ReviewCreateDto reviewCreateDto) throws Exception {
        Member member = memberRepository.findById(reviewCreateDto.getUid()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Review review = reviewCreateDto.toEntity(member);
        member.addReview(review);
    }

    // 리뷰 삭제
    public void deleteReview(Long reviewId) throws Exception {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()->new RuntimeException("해당하는 리뷰 정보가 없습니다"));
        review.getWriter().removeReview(review); // 리뷰 작성자의 리뷰 리스트에서 해당 리뷰 삭제
        reviewRepository.delete(review); // 리뷰 테이블에서 해당 리뷰 삭제
    }

    // 스크랩 등록
    public void createScrap(ScrapCreateDto scrapCreateDto) {
        Member member = memberRepository.findById(scrapCreateDto.uid()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Scrap scrap = scrapCreateDto.toEntity(member);
        member.addScrap(scrap);
    }

}
