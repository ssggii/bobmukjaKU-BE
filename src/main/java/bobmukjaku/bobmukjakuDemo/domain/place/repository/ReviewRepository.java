package bobmukjaku.bobmukjakuDemo.domain.place.repository;

import bobmukjaku.bobmukjakuDemo.domain.place.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
