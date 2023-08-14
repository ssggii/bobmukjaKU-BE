package bobmukjaku.bobmukjakuDemo.domain.chatroom.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.FilterInfo;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.*;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.service.ChatRoomService;
import bobmukjaku.bobmukjakuDemo.domain.chatting.ChatModel;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.service.MemberService;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    /*
     * <모집방 API>
     * 모집방 개설
     * 모집방 참여자 추가
     * 모집방 조회 - 전체 조회, 방 id로 모집방 조회, 방 id로 참여자 조회, uid로 참여 중인 모집방 조회
     * 모집방 필터링 - 종합 필터링
     * 모집방 삭제 - 자동 종료, 모집방 나가기
     * 메시지 전송
     * 필터목록 조회
     *
     * */

    private final ChatRoomService chatRoomService;

    // 모집방 개설
    @PostMapping("/chatRoom")
    public ResponseEntity openChatRoom(@RequestBody ChatRoomCreateDto chatRoomCreateDto) throws Exception {
        ChatRoomInfoDto info = chatRoomService.createChatRoom(chatRoomCreateDto, SecurityUtil.getLoginUsername());
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    // 모집방 참여자 추가
    @PostMapping("/chatRoom/member")
    public ResponseEntity<Boolean> addParticipantToChatRoom(@RequestBody AddMemberDto addMemberDto) {
        Long roomId = addMemberDto.roomId();
        Long uid = addMemberDto.uid();
        return ResponseEntity.ok(chatRoomService.addMemberToChatRoom(roomId, uid));
    }

    // 모집방 전체 조회
    @GetMapping("/chatRooms/info")
    public ResponseEntity getAllChatRooms() throws Exception {
        List<ChatRoomInfoDto> allChatRooms = chatRoomService.getAllChatRooms();
        return new ResponseEntity<>(allChatRooms, HttpStatus.OK);
    }

    // 방 id로 모집방 조회
    @GetMapping("/chatRoom/info/1/{roomId}")
    public ResponseEntity getChatRoomInfo(@PathVariable("roomId")Long roomId) throws Exception {
        ChatRoomInfoDto roomInfo = chatRoomService.getChatRoomInfo(roomId);
        return new ResponseEntity(roomInfo, HttpStatus.OK);
    }

    // 방 id로 참여자 조회
    @GetMapping("/chatRoom/joiners/{roomId}")
    public ResponseEntity getChatRoomJoiners(@PathVariable("roomId")Long roomId) throws Exception {
        List<MemberInfoDto> joinerList = chatRoomService.getChatRoomJoinerInfo(roomId);
        return new ResponseEntity(joinerList, HttpStatus.OK);
    }

    // uid로 참여 중인 모집방 조회
    @GetMapping("/chatRoom/info/2/{uid}")
    public ResponseEntity getChatRoomInfoByUid(@PathVariable("uid") Long uid) throws Exception {
        List<ChatRoomInfoDto> chatRoomInfoList = chatRoomService.getChatRoomInfoByUid(uid);
        return new ResponseEntity(chatRoomInfoList, HttpStatus.OK);
    }

    // 종합 필터링
    @PostMapping("/chatRooms/filtered")
    public ResponseEntity getFilteredChatRooms(@RequestBody List<FilterInfo> filters) throws Exception {

        List<ChatRoomInfoDto> chatRoomInfoDtoList = new ArrayList<>();

        if(filters != null && !filters.isEmpty()){
            chatRoomService.saveFilterInfo(filters); // 필터링 호출 시 인자로 받은 리스트 저장
            chatRoomInfoDtoList = chatRoomService.getChatRoomsByFilterng(filters);
            if(chatRoomInfoDtoList == null){
                return new ResponseEntity("검색 결과가 없습니다", HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("인자가 null 값입니다");
        }

        return new ResponseEntity(chatRoomInfoDtoList, HttpStatus.OK);
    }

    // 필터 조회
    @GetMapping("/filter/info")
    public ResponseEntity getMyFilterInfo(HttpServletResponse response) throws Exception {
        List<FilterInfoDto> filterInfoList = chatRoomService.getMyFilterInfo();
        return new ResponseEntity(filterInfoList, HttpStatus.OK);
    }

    // 모집방 나가기
    @PostMapping("/chatRoom/member/exit")
    public ResponseEntity<Boolean> exitChatRoom(@RequestBody ChatRoomExitDto chatRoomExitDto) throws Exception {
        Long roodId = chatRoomExitDto.roomId();
        Long uid = chatRoomExitDto.uid();
        return new ResponseEntity(chatRoomService.exitChatRoom(roodId, uid), HttpStatus.OK);
    }

}
