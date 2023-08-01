package bobmukjaku.bobmukjakuDemo.domains.member.controller;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
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
    private String nickName = "shinD cute";
/*    private Integer rate = 40;
    private String profileColor = "bg18";
    private LocalDate certificatedAt = LocalDate.now();*/

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

    /*private void signUpFail(String signUpData) throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isBadRequest());
    }*/

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
    public void 회원가입_성공() throws Exception { // 수정 필요
        // given
        objectMapper.registerModule(new JavaTimeModule());
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
        signUp(noUsernameSignUpData);//예외가 발생하더라도 상태코드는 200
        signUp(noPasswordSignUpData);//예외가 발생하더라도 상태코드는 200
        signUp(noNickNameSignUpData);//예외가 발생하더라도 상태코드는 200

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
        map.put("nickName", nickName+"변경");
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
        assertThat(member.getMemberNickName()).isEqualTo(nickName+"변경");
        assertThat(member.getProfileColor()).isEqualTo("bg수정");
        assertThat(memberRepository.findAll().size()).isEqualTo(1);

    }
}
