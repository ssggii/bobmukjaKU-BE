package bobmukjaku.bobmukjakuDemo.domain.chatroom.service;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.FilterInfoRepository;
import bobmukjaku.bobmukjakuDemo.domain.memberchatroom.MemberChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.memberchatroom.repository.MemberChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoomSpecification;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.FilterInfo;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.FilterInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.ChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import com.google.firebase.messaging.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final FilterInfoRepository filterInfoRepository;

    // 모집방 개설
    public ChatRoomInfoDto createChatRoom(ChatRoomCreateDto chatRoomCreateDto, String username) throws Exception {
        Member host = memberRepository.findByMemberEmail(username)
                .orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        ChatRoom createdChatRoom = chatRoomCreateDto.toEntity();

        MemberChatRoom memberChatRoomInfo = new MemberChatRoom(host, createdChatRoom); // member-chatroom 매핑 정보 생성

        host.addChatRoom(memberChatRoomInfo); // host 모집방 목록에 createdChatRoom 추가
        createdChatRoom.addParticipant(memberChatRoomInfo); // createdChatRoom 참여자 목록에 host 추가

        ChatRoom savedEntity = chatRoomRepository.save(createdChatRoom);

        //참가자들에게 보낼 메시지를 예약한다.
        System.out.println("방id  :   " + savedEntity.getChatRoomId() + "\n\n\n");
        reserveNotification(savedEntity.getChatRoomId(), savedEntity.getMeetingDate(), savedEntity.getEndTime());

        return new ChatRoomInfoDto(createdChatRoom);
    }

    // 모집방 참여자 추가
    public Boolean addMemberToChatRoom(Long roomId, Long uid){
        Boolean result = null;
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("모집방을 찾을 수 없습니다. 모집방 ID: " + roomId));
        Member joiner = memberRepository.findById(uid)
                .orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다. 회원 UID: " + uid));

        MemberChatRoom memberChatRoomInfo = new MemberChatRoom(joiner, chatRoom);

        if(chatRoom.getCurrentNum() < chatRoom.getTotal()){ // 참여 가능한 방인지 검사
            List<Long> currentChatRoomIdList = joiner.getJoiningRooms().stream().map(memberChatRoom -> memberChatRoom.getChatRoom().getChatRoomId()).collect(Collectors.toList());
            if (currentChatRoomIdList.contains(roomId)){ // 이미 참여한 방인지 검사
                System.out.println("이미 가입한 모집방입니다");
                result = false;
            }
            chatRoom.addParticipant(memberChatRoomInfo);
            joiner.addChatRoom(memberChatRoomInfo);
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

    // 필터링
    public List<ChatRoomInfoDto> getChatRoomsFiltered(List<FilterInfo> filters) throws Exception {
        List<Specification<ChatRoom>> specifications = new ArrayList<>();

        for (FilterInfo filter : filters) {
            Specification<ChatRoom> specification = ChatRoomSpecification.createSpecification(filter, memberRepository);
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

    // 필터 조회
    public List<FilterInfoDto> getMyFilterInfo() throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<FilterInfoDto> filterList = member.getFilterList().stream().map(filterInfo -> filterInfo.toDto(filterInfo)).collect(Collectors.toList());
        return filterList;
    }

    // 필터 저장
    public List<FilterInfo> updateFilterInfo(List<FilterInfoDto> filters) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        List<Long> toDeleteIds = member.getFilterList().stream().map(filterInfo -> filterInfo.getFilterId()).collect(Collectors.toList());
        if(toDeleteIds != null && !toDeleteIds.isEmpty()){ // 기존에 필터 목록이 있다면
            for(Long id : toDeleteIds)
                filterInfoRepository.deleteById(id); // 이전 필터 정보 모두 삭제
        }

        List<FilterInfo> filterInfoList = filters.stream().map(filterInfoDto -> filterInfoDto.toEntity(member)).collect(Collectors.toList());
        member.updateFilterInfo(filterInfoList); // 새로운 필터 목록 저장

        return filterInfoList;
    }

    // 모집방 나가기
    public Boolean exitChatRoom(Long roodId, Long uid) throws Exception {
        Boolean result = false;
        Member member = memberRepository.findById(uid).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        ChatRoom chatRoom = chatRoomRepository.findById(roodId).orElseThrow(()->new RuntimeException("존재하지 않는 모집방입니다."));
        MemberChatRoom memberChatRoom = memberChatRoomRepository.findMemberChatRoomByChatRoomAndAndJoiner(chatRoom, member).orElseThrow(()->new RuntimeException("잘못된 모집방 가입 정보입니다"));

        if(memberChatRoom != null){
            member.deleteChatRoom(memberChatRoom); // member의 참여 모집방 목록에서 해당 모집방 삭제
            chatRoom.deleteParticipant(memberChatRoom); // chatRoom의 참여자 목록에서 해당 참여자 삭제
            if(chatRoom.getCurrentNum() == 0) {
                chatRoomRepository.delete(chatRoom); // 마지막 참여자인 경우 모집방 삭제
            }
            result = true;
        }

        return result;
    }

    //종료시간에 참여자들에게 알림을 보내도록 예약
    public void reserveNotification(Long roomId, LocalDate date, LocalTime time) throws Exception{
        Calendar endAt = Calendar.getInstance();
        //endAt.set(date.getYear(), date.getMonth().getValue()-1, date.getDayOfMonth(), time.getHour(), time.getMinute());
        endAt.set(2023,9,2,18,2);
        Date taskTime = new Date(endAt.getTimeInMillis());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //종료시간에 모집방id를 주제로 메시지(알림)을 전송하도록 작업 예약
                String topic = roomId.toString();
                Message message = Message.builder()
                        //.setNotification(Notification.builder().setTitle("제목" + topic).setBody("내용이다.").build())
                        //.setAndroidConfig(AndroidConfig.builder().setNotification(AndroidNotification.builder().setClickAction("FCM_EXE_ACTIVITY").build()).build())
                        //.putData("click_action", "FCM_EXE_ACTIVITY")
                        .putData("roomId", roomId.toString())
                        .setTopic(topic)
                        .build();

                try{
                    FirebaseMessaging.getInstance().send(message);
                }catch (FirebaseMessagingException e){
                    throw new RuntimeException(e);
                }
            }
        }, taskTime);
    }
}
