package bobmukjaku.bobmukjakuDemo.domain.place.repository;

import bobmukjaku.bobmukjakuDemo.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, String> {

    Optional<Place> findByPlaceId(String placeId); // 음식점 ID로 PLace 객체 찾기

}
