package bobmukjaku.bobmukjakuDemo.domain.chatting.service;

import bobmukjaku.bobmukjakuDemo.domain.chatting.ChatModel;
import bobmukjaku.bobmukjakuDemo.domain.chatting.ProfanityResponse;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
public class ChattingService {

    // 파이어베이스로 메시지 전송
    public void sendMessageToFireBase(ChatModel md) throws Exception {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("/chatRoom/"+md.getChatRoomId()+"/message");
        ref.push().setValue(md, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                System.out.println(md.getMessage());
                System.out.println(error.getMessage());
            }

        });
    }

    // 욕설 감지
    public Boolean inspectBadWord(String message) throws Exception {
        WebClient client = WebClient.builder()
                .baseUrl("http://43.200.23.47:5000")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String jsonBody = "{\"message\":\"" + message + "\"}";

        ProfanityResponse resultBadWordInspection = client.post()
                .uri("/check_profanity")
                .body(BodyInserters.fromValue(jsonBody))
                .retrieve()
                .bodyToMono(ProfanityResponse.class)
                .block();

        return resultBadWordInspection.getProfanity();
    }

}
