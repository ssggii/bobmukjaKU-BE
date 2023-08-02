package bobmukjaku.bobmukjakuDemo.domains.member.controller;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.RequestEntity.post;
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
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PasswordEncoder passwordEncoder;

    private static String SIGN_UP_URL = "/signUp";

    private String username = "username@konkuk.ac.kr";
    private String password = "password1234@";
    private String nickName = "ssggii";

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
        signUpFail(noUsernameSignUpData);//예외가 발생하면 상태코드는 400
        signUpFail(noPasswordSignUpData);//예외가 발생하면 상태코드는 400
        signUpFail(noNickNameSignUpData);//예외가 발생하면 상태코드는 400

        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void 회원정보수정_성공() throws Exception {
        //given
        objectMapper.registerModule(new JavaTimeModule());
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username, password, nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("nickName", "닉네임변경");
        map.put("profileColor", "bg수정");
        String updateMemberData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/member")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateMemberData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(member.getMemberNickName()).isEqualTo("닉네임변경");
        assertThat(member.getProfileColor()).isEqualTo("bg수정");
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    public void 비밀번호_수정_성공() throws Exception{
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("toBePassword", password+"!");
        String updatePassword = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                put("/member/password")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePassword))
                .andExpect(status().isOk());

        // then
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(()->new Exception("회원이 아닙니다."));
        assertThat(passwordEncoder.matches(password, member.getMemberPassword())).isFalse();
        assertThat(passwordEncoder.matches(password+"!", member.getMemberPassword())).isTrue();
    }

    @Test
    public void 비밀번호_수정_실패_현재_비밀번호_불일치() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password+1);
        map.put("toBePassword", password+"!@#");
        String updatePassword = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader, BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isBadRequest());

        // then
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(()->new Exception("회원이 아닙니다."));
        assertThat(passwordEncoder.matches(password, member.getMemberPassword())).isTrue();
        assertThat(passwordEncoder.matches(password+"!@#", member.getMemberPassword())).isFalse();

    }

    @Test
    public void 비밀번호_수정_실패_조건X() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("toBePassword", "12345");

        String updatePassword = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader, BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isBadRequest());

        // then
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(()->new Exception("회원이 아닙니다."));
        assertThat(passwordEncoder.matches(password, member.getMemberPassword())).isTrue();
        assertThat(passwordEncoder.matches("12345", member.getMemberPassword())).isFalse();

    }

    @Test
    public void 회원탈퇴_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        String updatePassword = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                delete("/member")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePassword))
                .andExpect(status().isOk());

        // then
        assertThrows(Exception.class, ()->memberRepository.findByMemberEmail(username).orElseThrow(()->new Exception("회원이 아닙니다.")));
    }

    @Test
    public void 회원탈퇴_실패_비밀번호_틀림() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password+11);
        String updatePassword = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader, BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isBadRequest());

        // then
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(()->new Exception("회원이 아닙니다."));
        assertThat(member).isNotNull();
    }

    @Test
    public void 회원탈퇴_실패_권한X() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        String updatePassword = objectMapper.writeValueAsString(map);

        // when
        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader, BEARER+accessToken+"1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isForbidden());

        // then
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(()->new Exception("회원이 아닙니다."));
        assertThat(member).isNotNull();
    }

    @Test
    public void 내정보_조회_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);

        String accessToken = getAccessToken();

        // when
        MvcResult result = mockMvc.perform(
                get("/member/"+0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER+accessToken))
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
                        get("/member")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken+"1"))
                .andExpect(status().isForbidden());

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
                        get("/member/"+uid)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken))
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
                        get("/member/5555")
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

        // when, then
        mockMvc.perform(
                        get("/member/1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken+1))
                .andExpect(status().isForbidden());

    }

    @Test
    public void 닉네임_중복_검사_성공() throws Exception {
        // given
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDto(username,password,nickName));
        signUp(signUpData);
        String accessToken = getAccessToken();

    }

    @Test
    public void 닉네임_중복_검사_실패() throws Exception {


    }
}