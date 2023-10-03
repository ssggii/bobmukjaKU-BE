package bobmukjaku.bobmukjakuDemo.domain.friend.service;

import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final MemberRepository memberRepository;


}
