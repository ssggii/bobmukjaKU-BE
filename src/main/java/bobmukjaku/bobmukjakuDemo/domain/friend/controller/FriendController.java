package bobmukjaku.bobmukjakuDemo.domain.friend.controller;

import bobmukjaku.bobmukjakuDemo.domain.friend.dto.BlockInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.friend.dto.FriendInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.friend.dto.FriendUpdateDto;
import bobmukjaku.bobmukjakuDemo.domain.friend.service.FriendService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FriendController {

    /*
     *
     * <친구관리 API>
     * 친구 등록
     * 친구 해제
     * 친구 조회 - 내 친구 목록 조회
     * 차단 등록
     * 차단 해제
     * 차단 조회 - 내 차단 목록 조회
     *
     * */

    private final FriendService friendService;

    // 친구 등록
    @PostMapping("/friend/registering")
    public void createFriend(@RequestBody FriendUpdateDto friendUpdateDto) throws Exception {
        friendService.createFriend(friendUpdateDto);
    }

    // 친구 해제
    @PostMapping("/friend/removing")
    public void deleteFriend(@RequestBody FriendUpdateDto friendUpdateDto) throws Exception {
        friendService.deleteFriend(friendUpdateDto);
    }

    // 내 친구 목록 조회
    @GetMapping("/friend/all")
    public ResponseEntity getMyFriends(HttpServletResponse response) throws Exception {
        List<FriendInfoDto> friendInfoList = friendService.getMyFriends();
        if(friendInfoList != null && !friendInfoList.isEmpty()){
            return new ResponseEntity(friendInfoList, HttpStatus.OK);
        } else {
            return new ResponseEntity("등록된 친구가 없습니다", HttpStatus.OK);
        }
    }

    // 차단 등록
    @PostMapping("/block/registering")
    public void createBlock(@RequestBody FriendUpdateDto friendUpdateDto) throws Exception {
        friendService.createBlock(friendUpdateDto);
    }

    // 차단 해제
    @PostMapping("/block/removing")
    public void deleteBlock(@RequestBody FriendUpdateDto friendUpdateDto) throws Exception {
        friendService.deleteBlock(friendUpdateDto);
    }

    // 내 차단 목록 조회
    @GetMapping("/block/all")
    public ResponseEntity getMyBlocks(HttpServletResponse response) throws Exception {
        List<BlockInfoDto> blockInfoList = friendService.getMyBlocks();
        if(blockInfoList != null && !blockInfoList.isEmpty()){
            return new ResponseEntity(blockInfoList, HttpStatus.OK);
        } else {
            return new ResponseEntity("차단한 사용자가 없습니다", HttpStatus.OK);
        }
    }
}
