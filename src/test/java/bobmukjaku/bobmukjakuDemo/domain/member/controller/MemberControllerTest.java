package bobmukjaku.bobmukjakuDemo.domain.member.controller;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.TimeBlock;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.TimeBlockDto;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.TimeBlockRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TimeBlockRepository timeBlockRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PasswordEncoder passwordEncoder;

    private static String SIGN_UP_URL = "/signUp";

    private String username = "username@konkuk.ac.kr";
    private String password = "password1234@";
    private String nickName = "ssggii";

    @AfterEach
    private void clear() {
        em.flush();
        em.clear();
    }

    private void signUp(String signUpData) throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());
    }

    private void signUpFail(String signUpData) throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isBadRequest());
    }

    @Value("${jwt.access.header}")
    private String accessHeader;
    private static final String BEARER = "Bearer ";

    private String getAccessToken() throws Exception {

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
    public void 회원가입_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, nickName));

        // when
        signUp(signUpData);

        // then
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(() -> new Exception("회원이 아닙니다."));
        assertThat(member.getMemberNickName()).isEqualTo(nickName);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    public void 회원가입_실패_필드가_없음() throws Exception {
        //given
        objectMapper.registerModule(new JavaTimeModule());
        String noUsernameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(null, password, nickName));
        String noPasswordSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, null, nickName));
        String noNickNameSignUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, null));

        //when, then
        signUpFail(noUsernameSignUpData); // 상태코드 400
        signUpFail(noPasswordSignUpData); // 상태코드 400
        signUpFail(noNickNameSignUpData); // 상태코드 400

        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }
    @Test
    public void 닉네임_중복인_경우_true() throws Exception {
        // given
        String signUpData1 = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData1);
        String checkNickname = nickName;

        // when, then
        mockMvc.perform(
                        get("/check/nickname")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(nickName))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void 닉네임_중복_아닌경우_false() throws Exception {

        // given
        String signUpData1 = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData1);
        String checkNickname = nickName+"변경";

        // when, then
        mockMvc.perform(
                        get("/check/nickname")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(checkNickname))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void 전체_회원_조회() throws Exception {
        // given
        String signUpData1 = objectMapper.writeValueAsString(new MemberSignUpDto("member1@konkuk.ac.kr", "pass1234@!","nick1"));
        String signUpData2 = objectMapper.writeValueAsString(new MemberSignUpDto("member2@konkuk.ac.kr", "pass1234@!","nick2"));
        String signUpData3 = objectMapper.writeValueAsString(new MemberSignUpDto("member3@konkuk.ac.kr", "pass1234@!","nick3"));
        signUp(signUpData1);
        signUp(signUpData2);
        signUp(signUpData3);

        // when, then
        mockMvc.perform(
                        get("/members/info")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 회원정보_조회_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();
        Long uid = memberRepository.findAll().get(0).getUid();

        // when
        MvcResult result = mockMvc.perform(
                        get("/member/info/"+uid)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        assertThat(member.getMemberEmail()).isEqualTo(username);
        assertThat(member.getMemberNickName()).isEqualTo(nickName);
    }

    @Test
    public void 회원정보_조회_실패_없는회원() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        // when
        MvcResult result = mockMvc.perform(
                        get("/member/info/5555")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken))
                .andExpect(status().isNotFound()).andReturn();

        // then
        Map<String, Integer> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(map.get("errorCode")).isEqualTo(MemberExceptionType.NOT_FOUND_MEMBER.getErrorCode());
    }

    @Test
    public void 회원정보_조회_실패_JWT없음() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();
        Long uid = memberRepository.findAll().get(0).getUid();

        // when, then
        mockMvc.perform(
                        get("/member/info/" + uid)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken+1))
                .andExpect(status().isForbidden());

    }
    @Test
    public void 내정보_조회_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        // when
        MvcResult result = mockMvc.perform(
                        get("/member/info")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        assertThat(member.getMemberEmail()).isEqualTo(username);
        assertThat(member.getMemberNickName()).isEqualTo(nickName);
    }

    @Test
    public void 내정보_조회_실패_JWT없음() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        // when, then
        mockMvc.perform(
                        get("/member/info")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken+1))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @Test
    public void 회원정보수정_성공() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
/*        map.put("nickName", "닉네임변경");
        map.put("profileColor", "bg수정");*/
        map.put("certificatedAt", "2023-08-09");
        map.put("rate", 60);
        map.put("toBePassword", "password!@");
        String updateMemberData = objectMapper.writeValueAsString(map);

        Member member = memberRepository.findByMemberEmail(username).orElseThrow(() -> new Exception("회원이 없습니다"));
        System.out.println("변경 전 비밀번호: " + member.getMemberPassword());

        //when
        mockMvc.perform(
                        put("/member/info")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateMemberData))
                .andDo(print())
                .andExpect(status().isOk());

        //then
/*        assertThat(member.getMemberNickName()).isEqualTo("닉네임변경");
        assertThat(member.getProfileColor()).isEqualTo("bg수정");*/
        assertThat(member.getCertificatedAt()).isEqualTo("2023-08-09");
        assertThat(member.getRate()).isEqualTo(60);
        assertThat(passwordEncoder.matches("password!@", member.getMemberPassword())).isEqualTo(true);

        /*System.out.println(member.getMemberNickName());
        System.out.println(member.getProfileColor());*/
        System.out.println(member.getCertificatedAt());
        System.out.println(member.getRate());
        System.out.println("변경 후 비밀번호: " + member.getMemberPassword());

    }

    @Test
    public void 회원탈퇴_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, nickName));
        signUp(signUpData);
        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        String usernameForWithdraw = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                        delete("/member/account")
                                .header(accessHeader, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(usernameForWithdraw))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThrows(Exception.class, () -> memberRepository.findByMemberEmail(username).orElseThrow(() -> new Exception("회원이 아닙니다.")));
    }

    @Test
    public void 회원탈퇴_실패_권한X() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);
        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        String usernameForWithdraw = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                        delete("/member/info")
                                .header(accessHeader, BEARER+accessToken+"1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(usernameForWithdraw))
                .andExpect(status().isForbidden());

        // then
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(()->new Exception("회원이 아닙니다."));
        assertThat(member).isNotNull();
    }

    // 시간표 저장
    @Test
    public void 시간표_저장_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);
        String accessToken = getAccessToken();
        Member member = memberRepository.findByMemberEmail(username).get();

        TimeBlockDto timeBlockDto1 = new TimeBlockDto(1, "09:00");
        TimeBlockDto timeBlockDto2 = new TimeBlockDto(2, "10:00");
        TimeBlockDto timeBlockDto3 = new TimeBlockDto(3, "11:00");
        List<TimeBlockDto> timeBlockDtoList = Arrays.asList(timeBlockDto1, timeBlockDto2, timeBlockDto3);
        assertThat(member.getTimeBlockList().size()).isEqualTo(0); // 시간표 저장 전

        // when
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/member/info/timeTable")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(timeBlockDtoList))
        )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(member.getTimeBlockList().size()).isEqualTo(timeBlockDtoList.size()); // 시간표 저장 후
        System.out.println("<시간표 저장 후>");
        member.getTimeBlockList().stream().map(timeBlock -> timeBlock.getDayOfWeek()).forEach(System.out::println);
        member.getTimeBlockList().stream().map(timeBlock -> timeBlock.getTime()).forEach(System.out::println);
        member.getTimeBlockList().stream().map(timeBlock -> timeBlock.getTimeBlockId()).forEach(System.out::println);
    }

    @Test
    public void 시간표_업데이트_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);
        String accessToken = getAccessToken();
        Member member = memberRepository.findByMemberEmail(username).get();

        TimeBlockDto timeBlockDto1 = new TimeBlockDto(1, "09:00");
        TimeBlockDto timeBlockDto2 = new TimeBlockDto(2, "10:00");
        TimeBlockDto timeBlockDto3 = new TimeBlockDto(3, "11:00");
        List<TimeBlockDto> timeBlockDtoList1 = Arrays.asList(timeBlockDto1, timeBlockDto2, timeBlockDto3);

        TimeBlockDto timeBlockDto4 = new TimeBlockDto(4, "09:00");
        TimeBlockDto timeBlockDto5 = new TimeBlockDto(5, "10:00");
        TimeBlockDto timeBlockDto6 = new TimeBlockDto(6, "11:00");
        TimeBlockDto timeBlockDto7 = new TimeBlockDto(7, "12:00");
        List<TimeBlockDto> timeBlockDtoList2 = Arrays.asList(timeBlockDto4, timeBlockDto5, timeBlockDto6, timeBlockDto7);

        assertThat(member.getTimeBlockList().size()).isEqualTo(0); // 시간표 저장 전

        // when
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/member/info/timeTable")
                                .header(accessHeader, BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(timeBlockDtoList1))
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/member/info/timeTable")
                                .header(accessHeader, BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(timeBlockDtoList2))
                )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(member.getTimeBlockList().size()).isEqualTo(timeBlockDtoList2.size()); // 시간표 저장 후
        assertThat(timeBlockRepository.findAll().size()).isEqualTo(timeBlockDtoList2.size());
        System.out.println("<시간표 저장 후>");
        member.getTimeBlockList().stream().map(timeBlock -> timeBlock.getDayOfWeek()).forEach(System.out::println);
        member.getTimeBlockList().stream().map(timeBlock -> timeBlock.getTime()).forEach(System.out::println);
        member.getTimeBlockList().stream().map(timeBlock -> timeBlock.getTimeBlockId()).forEach(System.out::println);
    }

    @Test
    public void 시간표_조회_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);
        String accessToken = getAccessToken();
        Member member = memberRepository.findByMemberEmail(username).get();

        TimeBlock timeBlock1 = TimeBlock.builder().dayOfWeek(1).time(LocalTime.parse("11:00")).build();
        TimeBlock timeBlock2 = TimeBlock.builder().dayOfWeek(3).time(LocalTime.parse("12:00")).build();
        TimeBlock timeBlock3 = TimeBlock.builder().dayOfWeek(4).time(LocalTime.parse("13:00")).build();
        List<TimeBlock> list = Arrays.asList(timeBlock1, timeBlock2, timeBlock3);
        timeBlockRepository.saveAll(list);
        member.updateTimeBlockInfo(list);

        // when
        mockMvc.perform(
                get("/timeTable")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void 메일인증_후_비밀번호_재설정_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, nickName));
        signUp(signUpData);
        Member member = memberRepository.findByMemberEmail(username).get();

        String newPassword = "password@!1";
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("newPassword", newPassword);
        String passwordUpdateDto = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                        put("/resetPassword")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(passwordUpdateDto)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(passwordEncoder.matches(newPassword, member.getMemberPassword())).isEqualTo(true);
    }

    @Test
    public void 비밀번호_재설정_실패_회원이_없는_경우() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);
        Member member = memberRepository.findByMemberEmail(username).get();

        String newPassword = "password@!1";
        Map<String, Object> map = new HashMap<>();
        map.put("username", "ssggii@konkuk.ac.kr");
        map.put("newPasword", newPassword);
        String passwordUpdateDto = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                        put("/resetPassword")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(passwordUpdateDto)
                )
                .andDo(print())
                .andExpect(status().is(404));

        // then
        assertThat(passwordEncoder.matches(newPassword, member.getMemberPassword())).isEqualTo(false);
    }
}
