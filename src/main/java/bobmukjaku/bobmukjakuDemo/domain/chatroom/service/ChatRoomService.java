package bobmukjaku.bobmukjakuDemo.domain.chatroom.service;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoomSpecification;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomInfo;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.ChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 모집방 개설
    public ChatRoomInfo createChatRoom(ChatRoomCreateDto chatRoomCreateDto, String username){
        Member host = memberRepository.findByMemberEmail(username)
                .orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        ChatRoom createdChatRoom = chatRoomCreateDto.toEntity();

        // host를 모집방의 참여자로 추가
        if (!createdChatRoom.isParticipant(host)) {
            host.addChatRoom(createdChatRoom);
            createdChatRoom.addParticipant(host);
            createdChatRoom.addCurrentNum(); // 방장을 참여자로 추가
        }

        chatRoomRepository.save(createdChatRoom);
        return new ChatRoomInfo(createdChatRoom);
    }

    // 모집방 참여자 추가
    public Boolean addMemberToChatRoom(Long roomId, Long uid){
        Boolean result = false;
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("모집방을 찾을 수 없습니다. 모집방 ID: " + roomId));
        Member joiner = memberRepository.findById(uid)
                .orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다. 회원 UID: " + uid));
        if(chatRoom.getCurrentNum() < chatRoom.getTotal()){
            chatRoom.addParticipant(joiner);
            joiner.addChatRoom(chatRoom);
            chatRoom.addCurrentNum(); // 참여 인원 ++
            result = true;
        } else {
            System.out.println("모집 정원 초과입니다");
        }
        return result;
    }

    // 전체 모집방 조회
    public List<ChatRoomInfo> getAllChatRooms() throws Exception {
        List<ChatRoom> allChatRooms = chatRoomRepository.findAll();
        List<ChatRoomInfo> result = allChatRooms.stream().map(ChatRoomInfo::new).collect(Collectors.toList());
        return result;
    }

    // 방 id로 모집방 조회
    public ChatRoomInfo getChatRoomInfo(Long id) throws Exception {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(() -> new Exception("존재하지 않는 모집방입니다"));
        return new ChatRoomInfo(chatRoom);
    }

    // 방 id로 참여자 조회
    public List<MemberInfoDto> getChatRoomJoinerInfo(Long roomId) throws Exception {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new Exception("존재하지 않는 모집방입니다"));
        List<MemberInfoDto> joinerInfoList = chatRoom.getParticipants().stream()
                .map(memberChatRoom -> new MemberInfoDto(memberChatRoom.getJoiner()))
                .collect(Collectors.toList());
        return joinerInfoList;
    }

    // 음식 분류로 모집방 조회
    public List<ChatRoomInfo> getChatRoomsByFood(String kindOfFood) throws Exception {
        Specification<ChatRoom> specification = ChatRoomSpecification.equalKindOfFood(kindOfFood);
        List<ChatRoom> chatRooms = chatRoomRepository.findAll(specification);
        List<ChatRoomInfo> chatRoomInfos = chatRooms.stream().map(ChatRoomInfo::new).collect(Collectors.toList());
        return chatRoomInfos;
    }

    // 정원으로 모집방 조회
    public List<ChatRoomInfo> getChatRoomsByTotal(int total) throws Exception {
        Specification<ChatRoom> specification = ChatRoomSpecification.equalTotal(total);
        List<ChatRoom> chatRooms = chatRoomRepository.findAll(specification);
        List<ChatRoomInfo> chatRoomInfos = chatRooms.stream().map(ChatRoomInfo::new).collect(Collectors.toList());
        return chatRoomInfos;
    }




}
