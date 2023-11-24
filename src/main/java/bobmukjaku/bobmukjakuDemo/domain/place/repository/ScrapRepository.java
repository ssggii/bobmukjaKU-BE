package bobmukjaku.bobmukjakuDemo.domain.place.repository;

import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.TopScrapRestaurantsInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    List<Scrap> findAllByPlaceId(String placeId); // 음식점 id로 스크랩 조회
    Long countByPlaceId(String placeId); // 특정 음식점의 스크랩 수 조회

    @Query("SELECT s.placeId AS placeId, s.placeName AS placeName, COUNT(s.placeId) AS scrapCount " +
            "FROM Scrap s " +
            "GROUP BY s.placeId, s.placeName " +
            "ORDER BY COUNT(s.placeId) DESC, s.placeId ASC")
    List<TopScrapRestaurantsInterface> findTop10PlacesByScrapCount(); // 상위 스크랩 음식점 조회

}
