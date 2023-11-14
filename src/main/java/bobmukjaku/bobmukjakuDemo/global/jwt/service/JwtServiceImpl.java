package bobmukjaku.bobmukjakuDemo.global.jwt.service;

import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService{

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    private final MemberRepository memberRepository;

    @Override
    public String createAccessToken(String username) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis()+accessTokenValidityInSeconds*1000))
                .withClaim(USERNAME_CLAIM, username)
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis()+refreshTokenValidityInSeconds*1000))
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public void updateRefreshToken(String username, String refreshToken) {
        memberRepository.findByMemberEmail(username)
                .ifPresentOrElse(
                        member -> member.updateRefreshToken(refreshToken),
                        () -> new Exception("회원 정보가 없습니다.")
                );
    }

    @Override
    public void deleteRefreshToken(String username) {
        memberRepository.findByMemberEmail(username)
                .ifPresentOrElse(
                        member -> member.deleteRefreshToken(),
                        () -> new Exception("회원 정보가 없습니다.")
                );
    }

    @Override
    public void sendBothToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
        tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);

    }

    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken){
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);


        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader)).filter(

                accessToken -> accessToken.startsWith(BEARER)

        ).map(accessToken -> accessToken.replace(BEARER, ""));
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader)).filter(

                refreshToken -> refreshToken.startsWith(BEARER)

        ).map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    @Override
    public Optional<String> extractUsername(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(USERNAME_CLAIM).asString());
        }catch (Exception e){
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    @Override
    public boolean isTokenValid(String token){
        try{
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        }catch (Exception e){
            System.out.println("유효하지 않은 Token입니다.");
            return false;
        }
    }
}
