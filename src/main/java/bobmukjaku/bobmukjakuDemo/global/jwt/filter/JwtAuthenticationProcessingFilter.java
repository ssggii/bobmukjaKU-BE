package bobmukjaku.bobmukjakuDemo.global.jwt.filter;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.global.jwt.service.JwtService;
import bobmukjaku.bobmukjakuDemo.global.utility.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private final String NO_CHECK_URL = "/login"; // "/login"으로 들어오는 요청에 대해서는 작동 X

    /*
    * 1. refresh token이 오는 경우 -> 토큰 검사 후 유효하면 access token 재발급, 필터 진행하지 않고 튕김
    * 2. access token만 오는 경우 -> 유저 정보 저장 후 필터 진행
    * */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        if(request.getRequestURI().equals(NO_CHECK_URL)){
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtService
                .extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null); // refreshToken이 없거나 유효하지 않다면 null 반환

        if(refreshToken != null){
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).ifPresent(
                accessToken -> {
                    if (redisUtil.hasKeyBlackList(accessToken)) {
                        try {
                            response.sendError(401);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    jwtService.extractUsername(accessToken).ifPresent(

                            username -> memberRepository.findByMemberEmail(username).ifPresent(
                                    this::saveAuthentication
                            )
                    );
                }
        );

        filterChain.doFilter(request,response);
    }

    private void saveAuthentication(Member member) {
        UserDetails user = User.builder()
                .username(member.getMemberEmail())
                .password(member.getMemberPassword())
                .roles(member.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authoritiesMapper.mapAuthorities(user.getAuthorities()));


        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        memberRepository.findByRefreshToken(refreshToken).ifPresent(
                member -> jwtService.sendAccessToken(response, jwtService.createAccessToken(member.getMemberEmail()))
        );

    }

}
