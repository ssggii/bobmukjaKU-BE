package bobmukjaku.bobmukjakuDemo.domain.friend.repository;

import bobmukjaku.bobmukjakuDemo.domain.friend.Friended;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friended, Long> {


}
