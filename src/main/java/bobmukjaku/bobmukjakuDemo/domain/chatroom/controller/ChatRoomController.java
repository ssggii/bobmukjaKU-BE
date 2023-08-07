package bobmukjaku.bobmukjakuDemo.domain.chatroom.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    /*
     * <모집방 API>
     * 모집방 생성
     * 모집방 조회
     * 모집방 입장
     * 모집방 삭제
     *
     * */

    private final ChatRoomService chatRoomService;

    // 모집방 생성
    @PostMapping("/chatRoom")
    @ResponseStatus(HttpStatus.OK)
    public void createChatRoom(@RequestBody ChatRoomCreateDto chatRoomCreateDto) throws Exception {
        chatRoomService.createChatRoom(chatRoomCreateDto);
    }

}
