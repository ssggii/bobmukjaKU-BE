package bobmukjaku.bobmukjakuDemo.domain.place.repository;

import bobmukjaku.bobmukjakuDemo.domain.place.Place;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.TopScrapRestaurantsInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, String> {

    Optional<Place> findByPlaceId(String placeId); // 음식점 ID로 PLace 객체 검색
    List<Place> findByPlaceNameContaining(char character); // 음식점 이름에 character를 포함하는 Place 객체 검색
    @Query("SELECT p FROM Place p " +
            "ORDER BY p.scrapCount DESC, " +
            "         p.reviewCount DESC, " +
            "         p.placeName ASC")
    List<TopScrapRestaurantsInterface> findTop10CustomSort(); // 스크랩&리뷰 수 기반 상위 음식점 검색

}