package bobmukjaku.bobmukjakuDemo.domain.chatroom.service;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.ChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 모집방 생성
    public void createChatRoom(ChatRoomCreateDto chatRoomCreateDto){
        ChatRoom createdChatRoom = chatRoomCreateDto.toEntity();
        chatRoomRepository.save(createdChatRoom);
    }

    // 모집방 참여자 추가
    public void joinChatRoom(Long chatRoomId, Long memberId){
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()-> new IllegalArgumentException("모집방을 찾을 수 없습니다. 모집방 ID: " + chatRoomId));
        Member joiner = memberRepository.findById(memberId)
                .orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다. 회원 UID: " + memberId));

        chatRoom.addParticipant(joiner);
    }

}
