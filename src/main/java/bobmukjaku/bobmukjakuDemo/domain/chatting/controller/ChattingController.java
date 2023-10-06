package bobmukjaku.bobmukjakuDemo.domain.chatting.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatting.ChatModel;
import bobmukjaku.bobmukjakuDemo.domain.chatting.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chattingService;

    // 메시지 전송
    @PutMapping("/message")
    @ResponseBody
    public ResponseEntity<Object> sendMessageToFireBase(@RequestBody ChatModel md) throws Exception {
        if(!md.getShareMessage()) {
            //플라스크에 요청을 보내서 메시지 욕설 여부 확인
            /*String message = md.getMessage();
            if (chattingService.inspectBadWord(md.getMessage())) {
                System.out.println("욕설감지\n");
                md.setMessage("##### " + message);
            }*/
            System.out.println("일반메시지\n");
        }
        chattingService.sendMessageToFireBase(md);
        return ResponseEntity.ok().build();
    }

}
