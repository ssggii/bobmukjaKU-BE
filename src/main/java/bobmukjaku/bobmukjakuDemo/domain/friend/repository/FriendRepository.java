package bobmukjaku.bobmukjakuDemo.domain.friend.repository;

import bobmukjaku.bobmukjakuDemo.domain.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
