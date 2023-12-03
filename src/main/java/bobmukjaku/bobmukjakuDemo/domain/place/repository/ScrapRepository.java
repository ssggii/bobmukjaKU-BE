package bobmukjaku.bobmukjakuDemo.domain.place.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    List<Scrap> findAllByPlaceId(String placeId); // 음식점 id로 스크랩 조회
    Long countByPlaceId(String placeId); // 특정 음식점의 스크랩 수 조회

    @Query("SELECT s.placeId, COUNT(s.placeId) AS scrapCount " +
            "FROM Scrap s " +
            "GROUP BY s.placeId " +
            "ORDER BY COUNT(s.placeId) DESC, s.placeId ASC")
    List<Object[]> findTop10PlacesByScrapCount(); // 상위 스크랩 음식점 조회

    @Query("SELECT DISTINCT s.placeId FROM Scrap s")
    List<String> findDistinctPlaceIds(); // 전체 Scrap 엔티티에서 placeId 중복 없이 가져오기

}
