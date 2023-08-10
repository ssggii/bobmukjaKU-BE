package bobmukjaku.bobmukjakuDemo.domain.chatroom.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.AddMemberDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.service.ChatRoomService;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    /*
     * <모집방 API>
     * 모집방 개설
     * 모집방 참여자 추가
     * 모집방 조회 - 전체 조회, 방 id로 모집방 조회, 방 id로 참여자 조회, uid로 참여 중인 모집방 조회
     * 모집방 필터링 - 최근 순 정렬, 전체 필터링 기능
     * 모집방 삭제 - 자동 종료, 모집방 나가기
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
    public ResponseEntity getChatRoomInfoByUid(@PathVariable("uid")Long uid) throws Exception {
        List<ChatRoomInfoDto> chatRoomInfoList = chatRoomService.getChatRoomInfoByUid(uid);
        return new ResponseEntity(chatRoomInfoList, HttpStatus.OK);
    }

    // 음식 분류로 모집방 조회
    @GetMapping("/chatRoom/filter/1/{kindOfFood}")
    public ResponseEntity getChatRoomsByFood(@PathVariable("kindOfFood")String kindOfFood) throws Exception {
        List<ChatRoomInfoDto> roomInfoList = chatRoomService.getChatRoomsByFood(kindOfFood);
        return new ResponseEntity(roomInfoList, HttpStatus.OK);
    }

    // 정원으로 모집방 조회
    @GetMapping("/chatRoom/filter/2/{total}")
    public ResponseEntity getChatRoomsByTotal(@PathVariable("total")int total) throws Exception {
        List<ChatRoomInfoDto> roomInfoList = chatRoomService.getChatRoomsByTotal(total);
        return new ResponseEntity(roomInfoList, HttpStatus.OK);
    }

    // 다중 조건 필터링
    /*@GetMapping("/chatRoom/filter/all")
    public ResponseEntity<List<ChatRoom>> getChatRoomsByAllFilters(@RequestBody ChatRoomFIlterDto fIlterDto) {
        List<ChatRoom> filteredChatRooms = chatRoomService.getChatRoomsByAllFilters(fIlterDto);
        return new ResponseEntity(filteredChatRooms, HttpStatus.OK);
    }*/

}
