package bobmukjaku.bobmukjakuDemo.global.login.handler;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.global.jwt.service.JwtServiceImpl;
import bobmukjaku.bobmukjakuDemo.global.utility.RedisUtil;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService {

    private final RedisUtil redisUtil;
    private final JwtServiceImpl jwtService;

    public void logout(HttpServletRequest request) {
        String username = SecurityUtil.getLoginUsername();
        String accessToken = jwtService.extractAccessToken(request).get();
        jwtService.deleteRefreshToken(username); // refreshToken 삭제
        redisUtil.setBlackList(accessToken, "accessToken", 90); // Redis에 accessToken 등록
    }
}
