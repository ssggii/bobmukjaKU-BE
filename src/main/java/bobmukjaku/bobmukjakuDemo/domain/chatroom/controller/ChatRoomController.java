package bobmukjaku.bobmukjakuDemo.domain.chatroom.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.FilterInfo;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.AddMemberDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.service.ChatRoomService;
import bobmukjaku.bobmukjakuDemo.domain.chatting.ChatModel;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.service.MemberService;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
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
     * 모집방 필터링 - 최신 순 정렬, 필터링
     * 모집방 삭제 - 자동 종료, 모집방 나가기
     *
     * */

    private final ChatRoomService chatRoomService;
    private final MemberService memberService;

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

/*    @GetMapping("/member/info/{uid}")
    public ResponseEntity getInfo(@Valid @PathVariable("uid") Long id) throws Exception{
        MemberInfoDto info = memberService.getInfo(id);
        return new ResponseEntity(info, HttpStatus.OK);
    }*/

    // uid로 참여 중인 모집방 조회
    @GetMapping("/chatRoom/info/2/{uid}")
    public ResponseEntity getChatRoomInfoByUid(@PathVariable("uid") Long uid) throws Exception {
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

    // 최신 순 정렬
    @GetMapping("/chatRoom/filter/latest")
    public ResponseEntity getChatRoomsByLatest() throws Exception{
        List<ChatRoomInfoDto> roomInfoDtoList = chatRoomService.getChatRoomsByLatest();
        return new ResponseEntity(roomInfoDtoList, HttpStatus.OK);
    }

    // 필터링
    @PostMapping("/chatRooms/filtered")
    public ResponseEntity getFilteredChatRooms(@RequestBody List<FilterInfo> filters) throws Exception {

        List<ChatRoomInfoDto> chatRoomInfoDtoList = new ArrayList<>();

        if(filters != null && !filters.isEmpty()){
            chatRoomInfoDtoList = chatRoomService.getChatRoomsByFilterng(filters);
            if(chatRoomInfoDtoList == null){
                return new ResponseEntity("검색 결과가 없습니다", HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("인자가 null 값입니다");
        }

        return new ResponseEntity(chatRoomInfoDtoList, HttpStatus.OK);
    }

    //파이어베이스로 메시지 전송
    @PutMapping("/message")
    @ResponseBody
    public ResponseEntity<Object> sendMessageToFireBase(@RequestBody ChatModel md) throws Exception {
        //플라스크에 요청을 보내서 메시지 욕설 여부 확인
        String message = md.getMessage();
        if(memberService.inspectBadWord(md.getMessage())){
            System.out.println("욕설감지\n");
            md.setIsProfanity(true);
        }
        memberService.sendMessageToFireBase(md);
        return ResponseEntity.ok().build();
    }

}
