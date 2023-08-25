package bobmukjaku.bobmukjakuDemo.domain.place.controller;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.dto.MemberSignUpDto;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.Review;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        map.put("imageUrl", "리뷰사진1 링크");
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
        assertThat(reviewRepository.findAll().size()).isEqualTo(1);
        assertThat(member.getReviewList().size()).isEqualTo(1);
    }

    // 리뷰 삭제
    @Test
    public void 리뷰_삭제_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        Review review1 = Review.builder().placeId("음식점1").contents("너모 맛있어요").imageUrl("리뷰사진1 링크").writer(member).build();
        Review review2 = Review.builder().placeId("음식점2").contents("너모 맛있어요2").imageUrl("리뷰사진2 링크").build();
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        member.addReview(review1);

        Map<String, Object> map = new HashMap<>();
        map.put("reviewId", review1.getReviewId());

        assertThat(reviewRepository.findAll().size()).isEqualTo(2);
        assertThat(member.getReviewList().size()).isEqualTo(1);

        // when
        mockMvc.perform(
                delete("/place/review/info")
                        .header(accessHeader, BEARER+accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map))
                )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThrows(Exception.class, ()->reviewRepository.findById(review1.getReviewId()).orElseThrow(()->new Exception("존재하지 않는 리뷰입니다")));

    }

    // uid로 리뷰 조회
    @Test
    public void uid로_리뷰_조회_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        Review review1 = Review.builder().placeId("음식점1").contents("너모 맛있어요").imageUrl("리뷰사진1 링크").writer(member).build();
        Review review2 = Review.builder().placeId("음식점2").contents("너모 맛있어요2").imageUrl("리뷰사진2 링크").writer(member).build();
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        member.addReview(review1);
        member.addReview(review2);

        // when, then
        mockMvc.perform(
                get("/place/review/info/1/" + member.getUid())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER+accessToken)
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    // 음식점 id로 리뷰 조회
    @Test
    public void 음식점id로_리뷰_조회_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        Review review1 = Review.builder().placeId("음식점1").contents("너모 맛있어요1").imageUrl("리뷰사진1 링크").writer(member).build();
        Review review2 = Review.builder().placeId("음식점1").contents("너모 맛있어요2").imageUrl("리뷰사진2 링크").writer(member).build();
        Review review3 = Review.builder().placeId("음식점2").contents("너모 맛없어요").imageUrl("리뷰사진3 링크").writer(member).build();
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);
        member.addReview(review1);
        member.addReview(review1);
        member.addReview(review3);

        // when, then
        mockMvc.perform(
                        get("/place/review/info/2/음식점1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER+accessToken)
                )
                .andDo(print())
                .andExpect(status().isOk());
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

    // 스크랩 해제
    @Test
    public void 스크랩_해제_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        Scrap scrap1 = Scrap.builder().placeId("음식점1").member(member).build();
        Scrap scrap2 = Scrap.builder().placeId("음식점2").member(member).build();
        member.addScrap(scrap1);
        member.addScrap(scrap2);

        Map<String, Object> map = new HashMap<>();
        map.put("uid", member.getUid());
        map.put("placeId", scrap1.getPlaceId());

        assertThat(member.getScrapList().size()).isEqualTo(2);
        assertThat(scrapRepository.findAll().size()).isEqualTo(2);

        // when
        mockMvc.perform(
                        post("/place/scrap/remove")
                                .header(accessHeader, BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map))
                )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(scrapRepository.findAll().size()).isEqualTo(1);
        assertThat(member.getScrapList().size()).isEqualTo(1);

    }

    // uid로 스크랩 조회
    @Test
    public void uid로_스크랩_조회_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        Scrap scrap1 = Scrap.builder().placeId("음식점1").member(member).build();
        Scrap scrap2 = Scrap.builder().placeId("음식점2").member(member).build();
        member.addScrap(scrap1);
        member.addScrap(scrap2);

        assertThat(member.getScrapList().size()).isEqualTo(2);
        assertThat(scrapRepository.findAll().size()).isEqualTo(2);

        // when, then
        mockMvc.perform(
                        get("/place/scrap/info/1/" + member.getUid())
                                .header(accessHeader, BEARER+accessToken)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    // 음식점 id로 스크랩 조회
    @Test
    public void 음식점id로_스크랩_조회_성공() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member = memberRepository.findByMemberEmail(username).get();

        Scrap scrap1 = Scrap.builder().placeId("음식점1").member(member).build();
        Scrap scrap2 = Scrap.builder().placeId("음식점1").member(member).build();
        Scrap scrap3 = Scrap.builder().placeId("음식점2").member(member).build();
        scrapRepository.save(scrap1);
        scrapRepository.save(scrap2);
        scrapRepository.save(scrap3);

        assertThat(scrapRepository.findAll().size()).isEqualTo(3);

        // when, then
        mockMvc.perform(
                get("/place/scrap/info/2/음식점1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER+accessToken)
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    // 음식점 스크랩 수 조회
    @Test
    public void 음식점_스크랩_수_조회() throws Exception {
        // given
        signUp();
        String accessToken = login();
        Member member1 = memberRepository.findByMemberEmail(username).get();
        Member member2 = Member.builder().memberEmail("member2@konkuk.ac.kr").memberNickName("nick2").memberPassword("password2!").build();
        memberRepository.save(member2);

        Scrap scrap1 = Scrap.builder().placeId("음식점1").member(member1).build();
        Scrap scrap2 = Scrap.builder().placeId("음식점1").member(member2).build();
        scrapRepository.save(scrap1);
        scrapRepository.save(scrap2);

        // when, then
        mockMvc.perform(
                get("/place/scrap/count/음식점1")
                        .header(accessHeader, BEARER+accessToken)
                        .characterEncoding(StandardCharsets.UTF_8)
        )
                .andDo(print())
                .andExpect(status().isOk());

    }
}
