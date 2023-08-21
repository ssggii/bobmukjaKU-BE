package bobmukjaku.bobmukjakuDemo.domain.place.service;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.ScrapCreateDto;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {

    @Value("${app.firebase-bucket}")
    private String fireBaseBucket;

    private final MemberRepository memberRepository;

    // 이미지 다운로드 (미완)
    public byte[] downloadImage(String imagePath) {
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

    // 스크랩 등록
    public void createScrap(ScrapCreateDto scrapCreateDto) {
        Member member = memberRepository.findById(scrapCreateDto.uid()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Scrap scrap = scrapCreateDto.toEntity(member);
        member.addScrap(scrap);
    }

}
