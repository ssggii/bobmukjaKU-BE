package bobmukjaku.bobmukjakuDemo.domain.chatroom.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.AddMemberDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.ChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.service.ChatRoomService;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ChatRoomControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    private String username = "username@konkuk.ac.kr";
    private String password = "password1234@";
    private String nickName = "ssggii";

    private void signUp() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, nickName));
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/signUp")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());
    }

    @Value("${jwt.access.header}")
    private String accessHeader;
    private static final String BEARER = "Bearer ";

    private String login() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }

    @Test
    public void 모집방_생성_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        ChatRoomCreateDto chatRoomCreateDto = new ChatRoomCreateDto("모집방1", "2023-08-07", "17:30", "19:30", "한식", 4);

        // when
        mockMvc.perform(post("/chatRoom")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(chatRoomCreateDto)))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(chatRoomRepository.findAll().size()).isEqualTo(1);
        Long roomId = chatRoomRepository.findAll().get(0).getChatRoomId();
        System.out.println("생성된 roomId: " + roomId);
        ChatRoom findChatRoom = chatRoomRepository.findById(roomId).get();
        System.out.println( "참여자: "+ findChatRoom.getParticipants().size() + "명");
        System.out.println("<참여자 이메일>");
        findChatRoom.getParticipants().stream().map(r->r.getJoiner().getMemberEmail()).forEach(System.out::println);

    }

    // 모집방 참여자

    @Test
    public void 모집방_참여자_추가_성공() throws Exception {
        // Given
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName("모집방")
                .meetingDate(LocalDate.parse("2023-08-27"))
                .kindOfFood("일식")
                .startTime(LocalTime.parse("17:30"))
                .endTime(LocalTime.parse("20:00"))
                .total(4)
                .build();

        chatRoomRepository.save(chatRoom);
        System.out.println("개설된 방 이름: " + chatRoomRepository.findAll().get(0).getRoomName());
        System.out.println("개설된 방 ID: " + chatRoomRepository.findAll().get(0).getChatRoomId());
        System.out.println("현재 인원: " + chatRoomRepository.findAll().get(0).getCurrentNum());

        signUp();
        String accessToken = login();

        Long roomId = chatRoomRepository.findAll().get(0).getChatRoomId();
        System.out.println("roomId: " + roomId);
        Long uid = memberRepository.findAll().get(0).getUid();
        System.out.println("uid: " + uid);

        AddMemberDto chatRoomMemberDto = new AddMemberDto(roomId, uid);

        // When
        mockMvc.perform(post("/chatRoom/member")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatRoomMemberDto)))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        ChatRoom findChatRoom = chatRoomRepository.findById(roomId).orElse(null);
        System.out.println("<참여자 닉네임>");
        chatRoomRepository.findById(findChatRoom.getChatRoomId()).get().getParticipants().stream().map(m->m.getJoiner().getMemberNickName()).forEach(System.out::println);
    }

}
