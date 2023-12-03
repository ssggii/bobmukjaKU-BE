package bobmukjaku.bobmukjakuDemo.domain.place.repository;

import bobmukjaku.bobmukjakuDemo.domain.place.Review;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByPlaceId(String placeId);
    Long countByPlaceId(String placeId); // 특정 음식점의 리뷰 수 조회
    @Query("SELECT DISTINCT r.placeId FROM Review r")
    List<String> findDistinctPlaceIds(); // 전체 Review 엔티티에서 placeId 중복 없이 가져오기
}
