package bobmukjaku.bobmukjakuDemo.domain.friend.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.ChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.FilterInfoRepository;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.service.ChatRoomService;
import bobmukjaku.bobmukjakuDemo.domain.friend.Friend;
import bobmukjaku.bobmukjakuDemo.domain.friend.dto.FriendInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.friend.dto.FriendUpdateDto;
import bobmukjaku.bobmukjakuDemo.domain.friend.service.FriendService;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.memberchatroom.repository.MemberChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FriendControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    FriendService friendService;

    @Autowired
    MemberRepository memberRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    private String username = "username@konkuk.ac.kr";
    private String password = "password1234@";
    private String nickName = "ssggii";

    @AfterEach
    private void clear() {
        em.flush();
        em.clear();
    }

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

    /*
     * 친구_등록_성공
     * 친구_해제_성공
     * 친구_목록_조회_성공
     * 차단_등록_성공
     * 차단_해제_성공
     * 차단_목록_조회_성공
     * */

    @Test
    public void 친구_등록_성공() throws Exception {

        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto("username2@konkuk.ac.kr", "password2!@#", "ssggii2"));
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/signUp")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());

        signUp();
        String accessToken = login();

        Member member = memberRepository.findByMemberEmail(username).get();
        Member friend = memberRepository.findByMemberEmail("username2@konkuk.ac.kr").get();
        FriendUpdateDto friendUpdateDto = new FriendUpdateDto(friend.getUid());

        // when
        mockMvc.perform(post("/friend/registering")
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendUpdateDto)))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(memberRepository.findAll().size()).isEqualTo(2);
        assertThat(member.getFriendList().size()).isEqualTo(1);
        assertThat(member.getFriendList().get(0).getFriendUid()).isEqualTo(friend.getUid());
        assertThat(member.getFriendList().get(0).getIsBlock()).isEqualTo(false);

    }

    @Test
    public void 친구_해제_성공() throws Exception {

        // given
        signUp();
        String accessToken = login();

        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto("username2@konkuk.ac.kr", "password2!@#", "ssggii2"));
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/signUp")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());

        Member member1 = memberRepository.findByMemberEmail(username).get();
        Member member2 = memberRepository.findByMemberEmail("username2@konkuk.ac.kr").get();
        Friend friend = Friend.builder().member(member1).friendUid(member2.getUid()).isBlock(false).build();
        member1.addFriend(friend);

        // when
        mockMvc.perform(post("/friend/removing")
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new FriendUpdateDto(member2.getUid()))))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(memberRepository.findAll().size()).isEqualTo(2);
        assertThat(member1.getFriendList().size()).isEqualTo(0);

    }

    @Test
    public void 차단_등록_성공() throws Exception {

        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto("username2@konkuk.ac.kr", "password2!@#", "ssggii2"));
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/signUp")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());

        signUp();
        String accessToken = login();

        Member member = memberRepository.findByMemberEmail(username).get();
        Member friend = memberRepository.findByMemberEmail("username2@konkuk.ac.kr").get();
        FriendUpdateDto friendUpdateDto = new FriendUpdateDto(friend.getUid());

        // when
        mockMvc.perform(post("/block/registering")
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendUpdateDto)))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(memberRepository.findAll().size()).isEqualTo(2);
        assertThat(member.getFriendList().size()).isEqualTo(1);
        assertThat(member.getFriendList().get(0).getFriendUid()).isEqualTo(friend.getUid());
        assertThat(member.getFriendList().get(0).getIsBlock()).isEqualTo(true);

    }
}