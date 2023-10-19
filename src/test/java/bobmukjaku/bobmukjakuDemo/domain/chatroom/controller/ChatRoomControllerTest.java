package bobmukjaku.bobmukjakuDemo.domain.chatroom.controller;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.FilterInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.friend.Friend;
import bobmukjaku.bobmukjakuDemo.domain.member.TimeBlock;
import bobmukjaku.bobmukjakuDemo.domain.memberchatroom.MemberChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.memberchatroom.repository.MemberChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.FilterInfo;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.AddMemberDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.ChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.FilterInfoRepository;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.service.ChatRoomService;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.filter;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;

    @Autowired
    FilterInfoRepository filterInfoRepository;

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
    * 모집방_개설_성공 (완료)
    * 모집방_개설_실패_권한없음
    * 모집방_개설_실패_필드없음
    * */

    @Test
    public void 모집방_개설_성공() throws Exception {
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
        assertThat(memberChatRoomRepository.findAll().size()).isEqualTo(1);

        Long roomId = chatRoomRepository.findAll().get(0).getChatRoomId();
        System.out.println("생성된 roomId: " + roomId);
        ChatRoom findChatRoom = chatRoomRepository.findById(roomId).get();
        System.out.println( "참여자: "+ findChatRoom.getParticipants().size() + "명");
        System.out.println("<참여자 이메일>");
        findChatRoom.getParticipants().stream().map(r->r.getJoiner().getMemberEmail()).forEach(System.out::println);
    }

    /*
    * 모집방_참여자추가_성공 (완료)
    * 모집방_참여자추가_실패_이미_참여중인_참여자
    * 모집방_참여자추가_실패_권한없음
    * */

    // 모집방 참여자 추가
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

    /*
    * 모집방_전체_조회_성공 (완료)
    * 모집방_전체_조회_실패_권한없음
    * 모집방_방id로_조회_성공 (완료)
    * 모집방_방id로_조회_실패_권한없음
    * 모집방_방id로_참여자_조회_성공 (완료)
    * 모집방_방id로_참여자_조회_실패_권한없음
    * 모집방_방id로_참여자_조회_실패_없는방임
    * uid로_참여중인_모집방_조회_성공 (완료)
    * 전체_필터링_성공 (완료)
    * */

    @Test
    public void 모집방_전체_조회_성공() throws Exception {
        // given
        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-07", "17:30", "19:30", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto2 = new ChatRoomCreateDto("모집방2", "2023-08-07", "17:30", "19:30", "일식", 5);
        ChatRoomCreateDto chatRoomCreateDto3 = new ChatRoomCreateDto("모집방3", "2023-08-07", "17:30", "19:30", "중식", 6);
        chatRoomRepository.save(chatRoomCreateDto1.toEntity());
        chatRoomRepository.save(chatRoomCreateDto2.toEntity());
        chatRoomRepository.save(chatRoomCreateDto3.toEntity());

        signUp();
        String accessToken = login();

        // when, then
        mockMvc.perform(
                get("/chatRooms/info")
                        .header(accessHeader, BEARER+accessToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(chatRoomRepository.findAll().size()).isEqualTo(3);

    }

    @Test
    public void 모집방_방id로_조회_성공() throws Exception {
        // given
        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-07", "17:30", "19:30", "한식", 4);
        chatRoomRepository.save(chatRoomCreateDto1.toEntity());
        Long roomId = chatRoomRepository.findAll().get(0).getChatRoomId();

        signUp();
        String accessToken = login();

        // when, then
        mockMvc.perform(
                get("/chatRoom/info/1/"+roomId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER+accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 모집방_방id로_참여자_조회_성공() throws Exception {
        // given
        Member member1 = Member.builder().memberEmail("user1@konkuk.ac.kr").memberPassword("password1@").memberNickName("nick1").build();
        Member member2 = Member.builder().memberEmail("user2@konkuk.ac.kr").memberPassword("password2@").memberNickName("nick2").build();
        Member member3 = Member.builder().memberEmail("user3@konkuk.ac.kr").memberPassword("password3@").memberNickName("nick3").build();
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        ChatRoom chatRoom = ChatRoom.builder().roomName("모집방1").total(4).build();
        chatRoomRepository.save(chatRoom);

        chatRoomService.addMemberToChatRoom(chatRoom.getChatRoomId(), member1.getUid());
        chatRoomService.addMemberToChatRoom(chatRoom.getChatRoomId(), member2.getUid());
        chatRoomService.addMemberToChatRoom(chatRoom.getChatRoomId(), member3.getUid());

        assertThat(memberRepository.findAll().size()).isEqualTo(3);
        assertThat(chatRoomRepository.findAll().size()).isEqualTo(1);
        assertThat(memberChatRoomRepository.findAll().size()).isEqualTo(3);

        System.out.println("현재 인원: " + chatRoomRepository.findAll().get(0).getCurrentNum() + "명");
        System.out.println("memberchatroom 테이블 행: " + memberChatRoomRepository.findAll().size() + "개");

        signUp();
        String accessToken = login();
        Long roomId = chatRoomRepository.findAll().get(0).getChatRoomId();

        // when, then
        mockMvc.perform(
                        get("/chatRoom/joiners/" + roomId)
                                .header(accessHeader, BEARER+accessToken)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void uid로_참여중인_모집방_조회_성공() throws Exception {
        // given
        ChatRoom chatRoom1 = ChatRoom.builder().roomName("모집방1").startTime(LocalTime.parse("17:30")).endTime(LocalTime.parse("19:30")).kindOfFood("한식").meetingDate(LocalDate.parse("2023-08-07")).total(4).build();
        ChatRoom chatRoom2 = ChatRoom.builder().roomName("모집방2").startTime(LocalTime.parse("17:30")).endTime(LocalTime.parse("19:30")).kindOfFood("한식").meetingDate(LocalDate.parse("2023-08-07")).total(4).build();
        ChatRoom chatRoom3 = ChatRoom.builder().roomName("모집방3").startTime(LocalTime.parse("17:30")).endTime(LocalTime.parse("19:30")).kindOfFood("한식").meetingDate(LocalDate.parse("2023-08-07")).total(4).build();
        ChatRoom chatRoom4 = ChatRoom.builder().roomName("모집방4").startTime(LocalTime.parse("17:30")).endTime(LocalTime.parse("19:30")).kindOfFood("한식").meetingDate(LocalDate.parse("2023-08-07")).total(4).build();
        chatRoomRepository.saveAll(Arrays.asList(chatRoom1, chatRoom2, chatRoom3, chatRoom4));

        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();
        Long uid = member.getUid();

        chatRoomService.addMemberToChatRoom(chatRoom1.getChatRoomId(), uid);
        chatRoomService.addMemberToChatRoom(chatRoom2.getChatRoomId(), uid);
        chatRoomService.addMemberToChatRoom(chatRoom3.getChatRoomId(), uid);

        System.out.println("joiningRooms 개수: " + member.getJoiningRooms().size());
        System.out.println("memberChatRoomRepository개수: " + memberChatRoomRepository.findAll().size());

        // when, then
        assertThat(chatRoomRepository.findAll().size()).isEqualTo(4);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
        assertThat(memberChatRoomRepository.findAll().size()).isEqualTo(3);

        mockMvc.perform(
                get("/chatRoom/info/2/" + uid)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER+accessToken)
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    // 필터링
    @Test
    public void 필터링_성공() throws Exception {
        // given
        // 모집방 1~6이 있고,
        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-07", "17:30", "19:30", "한식", 2);
        ChatRoomCreateDto chatRoomCreateDto2 = new ChatRoomCreateDto("모집방2", "2023-08-07", "17:30", "19:30", "한식", 3);
        ChatRoomCreateDto chatRoomCreateDto3 = new ChatRoomCreateDto("모집방3", "2023-08-07", "17:30", "19:30", "일식", 4);
        ChatRoomCreateDto chatRoomCreateDto4 = new ChatRoomCreateDto("모집방4", "2023-08-08", "17:30", "19:30", "중식", 4);
        ChatRoomCreateDto chatRoomCreateDto5 = new ChatRoomCreateDto("모집방5", "2023-08-08", "17:30", "19:30", "중식", 4);
        ChatRoomCreateDto chatRoomCreateDto6 = new ChatRoomCreateDto("모집방6", "2023-08-08", "17:30", "19:30", "일식", 3);
        ChatRoom chatRoom1 = chatRoomCreateDto1.toEntity();
        ChatRoom chatRoom2 = chatRoomCreateDto2.toEntity();
        ChatRoom chatRoom3 = chatRoomCreateDto3.toEntity();
        ChatRoom chatRoom4 = chatRoomCreateDto4.toEntity();
        ChatRoom chatRoom5 = chatRoomCreateDto5.toEntity();
        ChatRoom chatRoom6 = chatRoomCreateDto6.toEntity();
        List<ChatRoom> initial = Arrays.asList(chatRoom1, chatRoom2, chatRoom3, chatRoom4, chatRoom5, chatRoom6);
        chatRoomRepository.saveAll(initial);

        // 음식-한식, 정원-4를 필터링 조건으로 선택했을 때
        List<FilterInfoDto> filters = new ArrayList<>();
        filters.add(new FilterInfoDto("kindOfFood", "중식"));
        filters.add(new FilterInfoDto("total", "4"));
        filters.add(new FilterInfoDto("latest", ""));

        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        // when, then
        mockMvc.perform(post("/chatRooms/filtered")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filters)))
                .andDo(print())
                .andExpect(status().isOk());

        memberRepository.findByMemberEmail(username).get().getFilterList().stream().map(filterInfo -> filterInfo.getFilterType()).forEach(System.out::println);
        memberRepository.findByMemberEmail(username).get().getFilterList().stream().map(filterInfo -> filterInfo.getFilterValue()).forEach(System.out::println);
        memberRepository.findByMemberEmail(username).get().getFilterList().stream().map(filterInfo -> filterInfo.getFilterId()).forEach(System.out::println);
        assertThat(filterInfoRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    public void 필터링_하면_필터목록_자동저장_성공() throws Exception {
        // given
        // 모집방 1~6이 있고,
        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-07", "17:30", "19:30", "한식", 2);
        ChatRoomCreateDto chatRoomCreateDto2 = new ChatRoomCreateDto("모집방2", "2023-08-07", "17:30", "19:30", "한식", 3);
        ChatRoomCreateDto chatRoomCreateDto3 = new ChatRoomCreateDto("모집방3", "2023-08-07", "17:30", "19:30", "일식", 4);
        ChatRoomCreateDto chatRoomCreateDto4 = new ChatRoomCreateDto("모집방4", "2023-08-08", "17:30", "19:30", "중식", 4);
        ChatRoomCreateDto chatRoomCreateDto5 = new ChatRoomCreateDto("모집방5", "2023-08-08", "17:30", "19:30", "중식", 4);
        ChatRoomCreateDto chatRoomCreateDto6 = new ChatRoomCreateDto("모집방6", "2023-08-08", "17:30", "19:30", "일식", 3);
        ChatRoom chatRoom1 = chatRoomCreateDto1.toEntity();
        ChatRoom chatRoom2 = chatRoomCreateDto2.toEntity();
        ChatRoom chatRoom3 = chatRoomCreateDto3.toEntity();
        ChatRoom chatRoom4 = chatRoomCreateDto4.toEntity();
        ChatRoom chatRoom5 = chatRoomCreateDto5.toEntity();
        ChatRoom chatRoom6 = chatRoomCreateDto6.toEntity();
        List<ChatRoom> initial = Arrays.asList(chatRoom1, chatRoom2, chatRoom3, chatRoom4, chatRoom5, chatRoom6);
        chatRoomRepository.saveAll(initial);

        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        // 음식-한식, 정원-4를 필터링 조건으로 선택했을 때
        List<FilterInfoDto> filters1 = new ArrayList<>();
        filters1.add(new FilterInfoDto("kindOfFood", "중식"));
        filters1.add(new FilterInfoDto("total", "4"));
        filters1.add(new FilterInfoDto("latest", ""));

        List<FilterInfoDto> filters2 = new ArrayList<>();
        filters2.add(new FilterInfoDto("latest", ""));
        filters2.add(new FilterInfoDto("kindOfFood", "한식"));
        filters2.add(new FilterInfoDto("total", "3"));

        // when, then
        mockMvc.perform(post("/chatRooms/filtered")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filters1)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/chatRooms/filtered")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filters2)))
                .andDo(print())
                .andExpect(status().isOk());

        memberRepository.findByMemberEmail(username).get().getFilterList().stream().map(filterInfo -> filterInfo.getFilterType()).forEach(System.out::println);
        memberRepository.findByMemberEmail(username).get().getFilterList().stream().map(filterInfo -> filterInfo.getFilterValue()).forEach(System.out::println);
        memberRepository.findByMemberEmail(username).get().getFilterList().stream().map(filterInfo -> filterInfo.getFilterId()).forEach(System.out::println);

        assertThat(filterInfoRepository.findAll().size()).isEqualTo(member.getFilterList().size());
    }


    @Test
    public void 필터링_조회결과_없음() throws Exception {
        // given
        // 모집방 1~6이 있고,
        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-07", "17:30", "19:30", "한식", 2);
        ChatRoomCreateDto chatRoomCreateDto2 = new ChatRoomCreateDto("모집방2", "2023-08-07", "17:30", "19:30", "한식", 3);
        ChatRoomCreateDto chatRoomCreateDto3 = new ChatRoomCreateDto("모집방3", "2023-08-07", "17:30", "19:30", "일식", 4);
        ChatRoomCreateDto chatRoomCreateDto4 = new ChatRoomCreateDto("모집방4", "2023-08-08", "17:30", "19:30", "중식", 4);
        ChatRoomCreateDto chatRoomCreateDto5 = new ChatRoomCreateDto("모집방5", "2023-08-08", "17:30", "19:30", "중식", 4);
        ChatRoomCreateDto chatRoomCreateDto6 = new ChatRoomCreateDto("모집방6", "2023-08-08", "17:30", "19:30", "일식", 3);
        ChatRoom chatRoom1 = chatRoomCreateDto1.toEntity();
        ChatRoom chatRoom2 = chatRoomCreateDto2.toEntity();
        ChatRoom chatRoom3 = chatRoomCreateDto3.toEntity();
        ChatRoom chatRoom4 = chatRoomCreateDto4.toEntity();
        ChatRoom chatRoom5 = chatRoomCreateDto5.toEntity();
        ChatRoom chatRoom6 = chatRoomCreateDto6.toEntity();
        List<ChatRoom> initial = Arrays.asList(chatRoom1, chatRoom2, chatRoom3, chatRoom4, chatRoom5, chatRoom6);
        chatRoomRepository.saveAll(initial);

        // 음식-한식, 정원-4를 필터링 조건으로 선택했을 때
        List<FilterInfo> filters = new ArrayList<>();
        filters.add(new FilterInfo("kindOfFood", "중식"));
        filters.add(new FilterInfo("total", "5"));

        signUp();
        String accessToken = login();

        // when, then
        mockMvc.perform(post("/chatRooms/filtered")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filters)))
                .andDo(print())
                .andExpect(status().isNoContent());

    }

    @Test
    public void uid로_참여모집방_조회_디버깅() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-09", "17:00", "19:00", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto2 = new ChatRoomCreateDto("모집방2", "2023-08-09", "17:00", "19:00", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto3 = new ChatRoomCreateDto("모집방3", "2023-08-09", "17:00", "19:00", "한식", 4);
        chatRoomService.createChatRoom(chatRoomCreateDto1, username); // 모집방1 개설
        chatRoomService.createChatRoom(chatRoomCreateDto2, username); // 모집방2 개설
        chatRoomService.createChatRoom(chatRoomCreateDto3, username); // 모집방3 개설

        // when, then
        mockMvc.perform(
                        get("/chatRoom/info/2/" + member.getUid())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken)
                )
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(chatRoomRepository.findAll().size()).isEqualTo(3); // 모집방 3개인지 확인
        assertThat(memberRepository.findAll().size()).isEqualTo(1); // 회원 1명인지 확인
        assertThat(memberChatRoomRepository.findAll().size()).isEqualTo(3); // 모집방 개설하면서 참여자 추가되었는지 확인

        // 회원 입장에서 모집방 목록 확인
        System.out.println("<joingRooms>");
        member.getJoiningRooms().stream().map(memberChatRoom -> memberChatRoom.getChatRoom().getRoomName()).forEach(System.out::println);

        // 모집방 입장에서 참여자 목록 확인
        System.out.println("<모집방1의 participants>");
        ChatRoom chatRoom1 = chatRoomRepository.findChatRoomByRoomName("모집방1").get();
        System.out.println("participants 리스트 행 개수: " + chatRoom1.getParticipants().size());
        chatRoom1.getParticipants().stream().map(memberChatRoom -> memberChatRoom.getJoiner().getMemberNickName()).forEach(System.out::println);

        System.out.println("<모집방2의 participants>");
        ChatRoom chatRoom2 = chatRoomRepository.findChatRoomByRoomName("모집방2").get();
        System.out.println("participants 리스트 행 개수: " + chatRoom2.getParticipants().size());
        chatRoom2.getParticipants().stream().map(memberChatRoom -> memberChatRoom.getJoiner().getMemberNickName()).forEach(System.out::println);

        System.out.println("<모집방3의 participants>");
        ChatRoom chatRoom3 = chatRoomRepository.findChatRoomByRoomName("모집방3").get();
        System.out.println("participants 리스트 행 개수: " + chatRoom3.getParticipants().size());
        chatRoom3.getParticipants().stream().map(memberChatRoom -> memberChatRoom.getJoiner().getMemberNickName()).forEach(System.out::println);


    }

    // 필터 조회
    @Test
    public void 필터목록_조회_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        if(member.getFilterList() != null){
            member.addFilterInfo(new FilterInfo("kindOfFood", ""));
            member.addFilterInfo(new FilterInfo("total", "4"));
            member.addFilterInfo(new FilterInfo("meetingDate", "2023-08-10"));
        } else {
            System.out.println("filterList가 null입니다.");
        }

        // when
        mockMvc.perform(
                get("/filter/info")
                        .header(accessHeader, BEARER+accessToken)
                        .characterEncoding(StandardCharsets.UTF_8)
        )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(filterInfoRepository.findAll().size()).isEqualTo(3);
        assertThat(member.getFilterList().size()).isEqualTo(3);
    }

    // 모집방 나가기
    @Test
    public void 모집방_나가기_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();

        Member member = memberRepository.findAll().get(0);
        ChatRoom chatRoom1 = ChatRoom.builder().roomName("모집방1").total(4).build();
        ChatRoom chatRoom2 = ChatRoom.builder().roomName("모집방2").total(4).build();
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);
        chatRoomService.addMemberToChatRoom(chatRoom1.getChatRoomId(), member.getUid()); // 모집방1에 member 입장
        chatRoomService.addMemberToChatRoom(chatRoom2.getChatRoomId(), member.getUid()); // 모집방2에 member 입장

        assertThat(memberRepository.findAll().size()).isEqualTo(1);
        assertThat(chatRoomRepository.findAll().size()).isEqualTo(2);
        assertThat(memberChatRoomRepository.findAll().size()).isEqualTo(2);
        assertThat(member.getJoiningRooms().size()).isEqualTo(2);
        System.out.println("<모집방 나가기 전>");
        member.getJoiningRooms().stream().map(memberChatRoom -> memberChatRoom.getChatRoom().getRoomName()).forEach(System.out::println);

        Map<String, Long> map = new HashMap<>();
        map.put("roomId", chatRoom2.getChatRoomId());
        map.put("uid", member.getUid());
        String data = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                post("/chatRoom/member/exit")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)
        )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
        assertThat(chatRoomRepository.findAll().size()).isEqualTo(1); // 모집방2 삭제되어야함
        chatRoomRepository.findAll().stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);
        assertThat(memberChatRoomRepository.findAll().size()).isEqualTo(1); // 모집방2 가입정보 삭제되어야 함
        assertThat(member.getJoiningRooms().size()).isEqualTo(1); // 모집방2가 joiningRooms 리스트에 없어야 함
        member.getJoiningRooms().stream().map(memberChatRoom -> memberChatRoom.getChatRoom().getRoomName()).forEach(System.out::println);
    }

    @Test
    public void 전체_필터링_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();
        String uid = String.valueOf(member.getUid());

        // 모집방 1~6 생성, 시간표 정보 저장
        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-17", "17:20", "19:30", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto2 = new ChatRoomCreateDto("모집방2", "2023-08-17", "17:30", "19:30", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto3 = new ChatRoomCreateDto("모집방3", "2023-08-17", "18:30", "19:30", "일식", 4);
        ChatRoomCreateDto chatRoomCreateDto4 = new ChatRoomCreateDto("모집방4", "2023-08-08", "17:30", "19:30", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto5 = new ChatRoomCreateDto("모집방5", "2023-08-08", "17:30", "19:30", "한식", 3);
        ChatRoomCreateDto chatRoomCreateDto6 = new ChatRoomCreateDto("모집방6", "2023-08-08", "17:30", "19:30", "일식", 3);
        ChatRoom chatRoom1 = chatRoomCreateDto1.toEntity();
        ChatRoom chatRoom2 = chatRoomCreateDto2.toEntity();
        ChatRoom chatRoom3 = chatRoomCreateDto3.toEntity();
        ChatRoom chatRoom4 = chatRoomCreateDto4.toEntity();
        ChatRoom chatRoom5 = chatRoomCreateDto5.toEntity();
        ChatRoom chatRoom6 = chatRoomCreateDto6.toEntity();
        List<ChatRoom> initial = Arrays.asList(chatRoom1, chatRoom2, chatRoom3, chatRoom4, chatRoom5, chatRoom6);
        chatRoomRepository.saveAll(initial);

        List<TimeBlock> timeBlockList = new ArrayList<>();
        member.getTimeBlockList().add(TimeBlock.builder().dayOfWeek(4).time(LocalTime.parse("17:00")).build());

        // 음식-한식, 정원-4, 시간표 ON를 필터링 조건으로 선택했을 때
        List<FilterInfoDto> filters = new ArrayList<>();
        filters.add(new FilterInfoDto("kindOfFood", "한식"));
        filters.add(new FilterInfoDto("total", "4"));
        filters.add(new FilterInfoDto("timeTable", uid));

        // when, then
        mockMvc.perform(post("/chatRooms/filtered")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filters)))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(filterInfoRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    public void 친구가_참여중인_모집방_필터링_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();
        String uid = String.valueOf(member.getUid());

        // 친구 1,2 생성
        Member member1 = Member.builder().memberNickName("friend1").memberEmail("friend1@konkuk.ac.kr").memberPassword("password1!@").build();
        Member member2 = Member.builder().memberNickName("friend2").memberEmail("friend2@konkuk.ac.kr").memberPassword("password1!@").build();
        memberRepository.save(member1);
        memberRepository.save(member2);
        member.addFriend(Friend.builder().member(member).friendUid(member1.getUid()).isBlock(false).build());
        member.addFriend(Friend.builder().member(member).friendUid(member2.getUid()).isBlock(false).build());

        // 모집방 1~4 생성
        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-17", "17:20", "19:30", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto2 = new ChatRoomCreateDto("모집방2", "2023-08-17", "17:30", "19:30", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto3 = new ChatRoomCreateDto("모집방3", "2023-08-17", "18:30", "19:30", "일식", 4);
        ChatRoomCreateDto chatRoomCreateDto4 = new ChatRoomCreateDto("모집방4", "2023-08-08", "17:30", "19:30", "한식", 4);
        ChatRoom chatRoom1 = chatRoomCreateDto1.toEntity();
        ChatRoom chatRoom2 = chatRoomCreateDto2.toEntity();
        ChatRoom chatRoom3 = chatRoomCreateDto3.toEntity();
        ChatRoom chatRoom4 = chatRoomCreateDto4.toEntity();
        List<ChatRoom> initial = Arrays.asList(chatRoom1, chatRoom2, chatRoom3, chatRoom4);
        chatRoomRepository.saveAll(initial);

        assertThat(memberRepository.findAll().size()).isEqualTo(3);
        assertThat(chatRoomRepository.findAll().size()).isEqualTo(4);
        assertThat(member.getFriendList().stream().filter(friend -> friend.getIsBlock().equals(false))).size().isEqualTo(2);

        // 친구1 모집방 1,2 참여
        MemberChatRoom memberChatRoom1 = new MemberChatRoom(member1, chatRoom1);
        MemberChatRoom memberChatRoom2 = new MemberChatRoom(member1, chatRoom2);
        member1.addChatRoom(memberChatRoom1);
        chatRoom1.addParticipant(memberChatRoom1);
        member1.addChatRoom(memberChatRoom2);
        chatRoom2.addParticipant(memberChatRoom2);

        // 친구2 모집방 2,3 참여
        MemberChatRoom memberChatRoom3 = new MemberChatRoom(member2, chatRoom2);
        MemberChatRoom memberChatRoom4 = new MemberChatRoom(member2, chatRoom3);
        member2.addChatRoom(memberChatRoom3);
        chatRoom2.addParticipant(memberChatRoom3);
        member2.addChatRoom(memberChatRoom4);
        chatRoom3.addParticipant(memberChatRoom4);

        // 친구가 참여하는 모집방을 필터링 조건으로 선택했을 때
        List<FilterInfoDto> filters = new ArrayList<>();
        filters.add(new FilterInfoDto("friend", uid));

        // when, then
        mockMvc.perform(post("/chatRooms/filtered")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filters)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 차단사용자가_참여중인_모집방_필터링_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();
        String uid = String.valueOf(member.getUid());

        // 친구 1,2 생성
        Member member1 = Member.builder().memberNickName("friend1").memberEmail("friend1@konkuk.ac.kr").memberPassword("password1!@").build();
        Member member2 = Member.builder().memberNickName("friend2").memberEmail("friend2@konkuk.ac.kr").memberPassword("password1!@").build();
        memberRepository.save(member1);
        memberRepository.save(member2);
        member.addFriend(Friend.builder().member(member).friendUid(member1.getUid()).isBlock(true).build());
        member.addFriend(Friend.builder().member(member).friendUid(member2.getUid()).isBlock(false).build());

        // 모집방 1~4 생성
        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-17", "17:20", "19:30", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto2 = new ChatRoomCreateDto("모집방2", "2023-08-17", "17:30", "19:30", "한식", 4);
        ChatRoomCreateDto chatRoomCreateDto3 = new ChatRoomCreateDto("모집방3", "2023-08-17", "18:30", "19:30", "일식", 4);
        ChatRoomCreateDto chatRoomCreateDto4 = new ChatRoomCreateDto("모집방4", "2023-08-08", "17:30", "19:30", "한식", 4);
        ChatRoom chatRoom1 = chatRoomCreateDto1.toEntity();
        ChatRoom chatRoom2 = chatRoomCreateDto2.toEntity();
        ChatRoom chatRoom3 = chatRoomCreateDto3.toEntity();
        ChatRoom chatRoom4 = chatRoomCreateDto4.toEntity();
        List<ChatRoom> initial = Arrays.asList(chatRoom1, chatRoom2, chatRoom3, chatRoom4);
        chatRoomRepository.saveAll(initial);

        assertThat(memberRepository.findAll().size()).isEqualTo(3);
        assertThat(chatRoomRepository.findAll().size()).isEqualTo(4);
        assertThat(member.getFriendList().stream().filter(friend -> friend.getIsBlock().equals(true))).size().isEqualTo(1);

        // 차단사용자 모집방 1,2 참여
        MemberChatRoom memberChatRoom1 = new MemberChatRoom(member1, chatRoom1);
        MemberChatRoom memberChatRoom2 = new MemberChatRoom(member1, chatRoom2);
        member1.addChatRoom(memberChatRoom1);
        chatRoom1.addParticipant(memberChatRoom1);
        member1.addChatRoom(memberChatRoom2);
        chatRoom2.addParticipant(memberChatRoom2);

        // 친구 모집방 2,3 참여
        MemberChatRoom memberChatRoom3 = new MemberChatRoom(member2, chatRoom2);
        MemberChatRoom memberChatRoom4 = new MemberChatRoom(member2, chatRoom3);
        member2.addChatRoom(memberChatRoom3);
        chatRoom2.addParticipant(memberChatRoom3);
        member2.addChatRoom(memberChatRoom4);
        chatRoom3.addParticipant(memberChatRoom4);

        // 차단한 사용자가 참여하는 모집방을 필터링 조건으로 선택했을 때
        List<FilterInfoDto> filters = new ArrayList<>();
        filters.add(new FilterInfoDto("block", uid));

        // when, then
        mockMvc.perform(post("/chatRooms/filtered")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filters)))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
