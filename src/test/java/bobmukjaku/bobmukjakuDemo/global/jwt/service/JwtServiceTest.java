package bobmukjaku.bobmukjakuDemo.global.jwt.service;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.Role;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class JwtServiceTest {

    @Autowired
    JwtService jwtService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    private String username = "username";

    @BeforeEach
    public void init(){
        Member member = Member.builder()
                .memberEmail(username).memberPassword("비밀번호")
                .memberNickName("닉네임").role(Role.USER).build();
        memberRepository.save(member);
        clear();
    }

    public void clear(){
        em.flush();
        em.clear();
    }

    private DecodedJWT getVerify(String token){
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
    }

    @Test
    public void accessToken_발급() throws Exception{
        // given, when
        String acccessToken = jwtService.createAccessToken(username);
        DecodedJWT verify = getVerify(acccessToken);
        String subject = verify.getSubject();
        String findUsername = verify.getClaim(USERNAME_CLAIM).asString();

        // then
        assertThat(findUsername).isEqualTo(username);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    public void refreshToken_발급() throws Exception{
        // given, when
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);
        String subject = verify.getSubject();
        String username = verify.getClaim(USERNAME_CLAIM).asString();

        // then
        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
        assertThat(username).isNull();
    }

    @Test
    public void refreshToken_업데이트() throws Exception{
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();
        Thread.sleep(2000);

        // when
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, reIssuedRefreshToken);
        clear();

        // then
        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());
        assertThat(memberRepository.findByRefreshToken(reIssuedRefreshToken).get().getMemberEmail()).isEqualTo(username);
    }

    @Test
    public void refreshToken_제거() throws Exception{
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();

        // when
        jwtService.deleteRefreshToken(username);
        clear();

        // then
        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());

        Member member = memberRepository.findByMemberEmail(username).get();
        assertThat(member.getRefreshToken()).isNull();
    }

    @Test
    public void 토큰_유효성_검사() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        //when, then
        assertThat(jwtService.isTokenValid(accessToken)).isTrue();
        assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
        assertThat(jwtService.isTokenValid(accessToken+"d")).isFalse();
        assertThat(jwtService.isTokenValid(accessToken+"d")).isFalse();

    }

    @Test
    public void AccessToken_헤더_설정() throws Exception{
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);

        // when
        jwtService.sendBothToken(mockHttpServletResponse, accessToken, refreshToken);

        // then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
    }

    @Test
    public void refreshToken_헤더_설정() throws Exception{
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);

        // when
        jwtService.sendBothToken(mockHttpServletResponse, accessToken, refreshToken);

        // then
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    public void 토큰_전송() throws Exception{
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        // when
        jwtService.sendBothToken(mockHttpServletResponse, accessToken, refreshToken);

        // then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException{

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        jwtService.sendBothToken(mockHttpServletResponse,accessToken,refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);
        httpServletRequest.addHeader(refreshHeader, BEARER+headerRefreshToken);

        return httpServletRequest;
    }

    @Test
    public void AccessToken_추출() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        //when
        String extractAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(()-> new Exception("토큰이 없습니다."));

        //then
        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(getVerify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(username);
    }


    @Test
    public void RefreshToken_추출() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        //when
        String extractRefreshToken = jwtService.extractRefreshToken(httpServletRequest).orElseThrow(()-> new Exception("토큰이 없습니다."));

        //then
        assertThat(extractRefreshToken).isEqualTo(refreshToken);
        assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }

    @Test
    public void Username_추출() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        String requestAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(()-> new Exception("토큰이 없습니다."));

        //when
        String extractUsername = jwtService.extractUsername(requestAccessToken).orElseThrow(()-> new Exception("토큰이 없습니다."));

        //then
        assertThat(extractUsername).isEqualTo(username);
    }
}
