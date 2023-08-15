package bobmukjaku.bobmukjakuDemo.domain.member.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.TimeBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeBlockRepository extends JpaRepository<TimeBlock, Long> {
}
