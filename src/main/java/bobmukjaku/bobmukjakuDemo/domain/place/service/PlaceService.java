package bobmukjaku.bobmukjakuDemo.domain.place.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PlaceService {

    @Value("${app.firebase-bucket}")
    private String fireBaseBucket;

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

}
