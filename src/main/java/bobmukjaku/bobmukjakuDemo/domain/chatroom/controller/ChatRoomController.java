package bobmukjaku.bobmukjakuDemo.domain.chatroom.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.AddMemberDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomInfo;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.service.ChatRoomService;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    /*
     * <모집방 API>
     * 모집방 개설
     * 모집방 참여자 추가
     * 모집방 조회
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

}
