package bobmukjaku.bobmukjakuDemo.domain.place.controller;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.repository.ReviewRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.repository.ScrapRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PlaceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    ReviewRepository reviewRepository;

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
                        post("/signUp")
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
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }

    // 이미지 업로드
    @Test
    public void 이미지_업로드_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();

        // 테스트할 이미지 파일 생성
        byte[] imageBytes = Files.readAllBytes(Paths.get("D:/2023 2학기/test.jpg"));
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", imageBytes);
        String fileName = "test.jpg";

        // when, then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/files")
                        .file(file)
                        .header(accessHeader, BEARER+accessToken)
                        .param("fileName", fileName))
                .andExpect(status().isOk())
                .andReturn();

    }

    // 리뷰 등록
    @Test
    public void 리뷰_등록_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        Map<String, Object> map = new HashMap<>();
        map.put("placeId", "음식점1");
        map.put("contents", "너모 맛있어요");
        map.put("imageName", "리뷰사진1.jpg");
        map.put("uid", member.getUid());

        // when
        mockMvc.perform(
                post("/place/review")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map))
        )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(member.getReviewList().size()).isEqualTo(1);
        assertThat(member.getReviewList().size()).isEqualTo(1);
    }

    // 스크랩 등록
    @Test
    public void 스크랩_등록_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        Map<String, Object> map = new HashMap<>();
        map.put("uid", member.getUid());
        map.put("placeId", "음식점1");

        // when
        mockMvc.perform(
                post("/place/scrap")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(objectMapper.writeValueAsString(map)))
        )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(scrapRepository.findAll().size()).isEqualTo(1);
        assertThat(member.getScrapList().size()).isEqualTo(1);

    }
}
