package bobmukjaku.bobmukjakuDemo.domain.friend.repository;

import bobmukjaku.bobmukjakuDemo.domain.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    void deleteFriendByFriendUid(Long friendUid); // 친구 또는 차단사용자의 uid와 일치하는 Friend 엔티티 삭제
}
