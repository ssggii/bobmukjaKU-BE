package bobmukjaku.bobmukjakuDemo.domain.chatroom.service;

import bobmukjaku.bobmukjakuDemo.domain.MemberChatRoom.MemberChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoomSpecification;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.FilterInfo;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomFIlterDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.ChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 모집방 개설
    public ChatRoomInfoDto createChatRoom(ChatRoomCreateDto chatRoomCreateDto, String username){
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
        return new ChatRoomInfoDto(createdChatRoom);
    }

    // 모집방 참여자 추가
    public Boolean addMemberToChatRoom(Long roomId, Long uid){
        Boolean result = null;
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("모집방을 찾을 수 없습니다. 모집방 ID: " + roomId));
        Member joiner = memberRepository.findById(uid)
                .orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다. 회원 UID: " + uid));
        System.out.println("currentNum: " + chatRoom.getCurrentNum());
        System.out.println("total: " + chatRoom.getTotal());
        if(chatRoom.getCurrentNum() < chatRoom.getTotal()){
            List<Long> currentChatRoomIdList = joiner.getJoiningRooms().stream().map(memberChatRoom -> memberChatRoom.getChatRoom().getChatRoomId()).collect(Collectors.toList());
            if (currentChatRoomIdList.contains(roomId)){
                System.out.println("이미 가입한 모집방입니다");
                result = false;
            }
            chatRoom.addParticipant(joiner);
            joiner.addChatRoom(chatRoom);
            chatRoom.addCurrentNum(); // 참여 인원 ++
            result = true;
        } else {
            System.out.println("모집 정원 초과입니다");
            result = false;
        }
        return result;
    }

    // 전체 모집방 조회
    public List<ChatRoomInfoDto> getAllChatRooms() throws Exception {
        List<ChatRoom> allChatRooms = chatRoomRepository.findAll();
        List<ChatRoomInfoDto> result = allChatRooms.stream().map(ChatRoomInfoDto::new).collect(Collectors.toList());
        return result;
    }

    // 방 id로 모집방 조회
    public ChatRoomInfoDto getChatRoomInfo(Long id) throws Exception {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(() -> new Exception("존재하지 않는 모집방입니다"));
        return new ChatRoomInfoDto(chatRoom);
    }

    // 방 id로 참여자 조회
    public List<MemberInfoDto> getChatRoomJoinerInfo(Long roomId) throws Exception {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new Exception("존재하지 않는 모집방입니다"));
        List<MemberInfoDto> joinerInfoList = chatRoom.getParticipants().stream()
                .map(memberChatRoom -> new MemberInfoDto(memberChatRoom.getJoiner()))
                .collect(Collectors.toList());
        return joinerInfoList;
    }

    // uid로 참여 중인 모집방 조회
    public List<ChatRoomInfoDto> getChatRoomInfoByUid(Long uid) throws Exception {
        Member member = memberRepository.findById(uid).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<ChatRoomInfoDto> chatRoomInfoDtoList = member.getJoiningRooms().stream()
                .map(memberChatRoom -> new ChatRoomInfoDto(memberChatRoom.getChatRoom()))
                .collect(Collectors.toList());
        return chatRoomInfoDtoList;
    }

    // 최신 순으로 정렬
    public List<ChatRoomInfoDto> getChatRoomsByLatest() {
        List<ChatRoom> sortedChatRooms = chatRoomRepository.findAllByOrderByCreatedAtDesc();
        List<ChatRoomInfoDto> result = sortedChatRooms.stream().map(ChatRoomInfoDto::new).collect(Collectors.toList());
        return result;
    }

    // 음식 분류로 모집방 조회
    public List<ChatRoomInfoDto> getChatRoomsByFood(String kindOfFood) throws Exception {
        Specification<ChatRoom> specification = ChatRoomSpecification.equalKindOfFood(kindOfFood);
        List<ChatRoom> chatRooms = chatRoomRepository.findAll(specification);
        List<ChatRoomInfoDto> chatRoomInfos = chatRooms.stream().map(ChatRoomInfoDto::new).collect(Collectors.toList());
        return chatRoomInfos;
    }

    // 정원으로 모집방 조회
    public List<ChatRoomInfoDto> getChatRoomsByTotal(int total) throws Exception {
        Specification<ChatRoom> specification = ChatRoomSpecification.equalTotal(total);
        List<ChatRoom> chatRooms = chatRoomRepository.findAll(specification);
        List<ChatRoomInfoDto> chatRoomInfos = chatRooms.stream().map(ChatRoomInfoDto::new).collect(Collectors.toList());
        return chatRoomInfos;
    }

    // 필터링
    public List<ChatRoomInfoDto> getChatRoomsByFilterng(List<FilterInfo> filters) throws Exception {

        List<Specification<ChatRoom>> specifications = new ArrayList<>();

        for (FilterInfo filter : filters) {
            Specification<ChatRoom> specification = ChatRoomSpecification.createSpecification(filter);
            if (specification != null)
                specifications.add(specification);
            else
                System.out.println("유효하지 않은 필터입니다.");
        }

        Specification<ChatRoom> combinedSpecification = ChatRoomSpecification.combineSpecifications(specifications);
        List<ChatRoom> chatRooms = chatRoomRepository.findAll(combinedSpecification);
        if(chatRooms.isEmpty()) {
            return null;
        }
        return chatRooms.stream().map(ChatRoomInfoDto::new).collect(Collectors.toList());

    }

}
