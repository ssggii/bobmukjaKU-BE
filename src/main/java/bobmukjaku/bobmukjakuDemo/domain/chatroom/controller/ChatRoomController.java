package bobmukjaku.bobmukjakuDemo.domain.chatroom.controller;

import bobmukjaku.bobmukjakuDemo.domain.MemberChatRoom.MemberChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.AddMemberDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomInfo;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.service.ChatRoomService;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import jakarta.validation.Valid;
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
     * 모집방 조회 - 전체 조회, 방 id로 모집방 조회, 방 id로 참여자 조회,
     *              최근순 조회, 마감 임박순 조회, 음식 카테고리별 조회, 정원 별 조회
     * 모집방 삭제
     *
     * */

    private final ChatRoomService chatRoomService;

    // 모집방 개설
    @PostMapping("/chatRoom")
    public ResponseEntity openChatRoom(@RequestBody ChatRoomCreateDto chatRoomCreateDto) throws Exception {
        ChatRoomInfo info = chatRoomService.createChatRoom(chatRoomCreateDto, SecurityUtil.getLoginUsername());
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
        List<ChatRoomInfo> allChatRooms = chatRoomService.getAllChatRooms();
        return new ResponseEntity<>(allChatRooms, HttpStatus.OK);
    }

    // 방 id로 모집방 조회
    @GetMapping("/chatRoom/info/{roomId}")
    public ResponseEntity getChatRoomInfo(@PathVariable("roomId")Long roomId) throws Exception {
        ChatRoomInfo roomInfo = chatRoomService.getChatRoomInfo(roomId);
        return new ResponseEntity(roomInfo, HttpStatus.OK);
    }

    // 음식 분류로 모집방 조회
    @GetMapping("/chatRoom/filter/1/{kindOfFood}")
    public ResponseEntity getChatRoomByFood(@PathVariable("kindOfFood")String kindOfFood) throws Exception {
        List<ChatRoomInfo> roomInfoList = chatRoomService.getChatRoomByFood(kindOfFood);
        return new ResponseEntity(roomInfoList, HttpStatus.OK);
    }

    // 정원으로 모집방 조회
    @GetMapping("/chatRoom/filter/2/{total}")
    public ResponseEntity getChatRoomByTotal(@PathVariable("total")int total) throws Exception {
        List<ChatRoomInfo> roomInfoList = chatRoomService.getChatRoomByTotal(total);
        return new ResponseEntity(roomInfoList, HttpStatus.OK);
    }

    // 방 id로 참여자 조회
    @GetMapping("/chatRoom/joiners/{roomId}")
    public ResponseEntity getChatRoomJoiners(@PathVariable("roomId")Long roomId) throws Exception {
        List<MemberInfoDto> joinerList = chatRoomService.getChatRoomJoinerInfo(roomId);
        return new ResponseEntity(joinerList, HttpStatus.OK);
    }

}
