package bobmukjaku.bobmukjakuDemo.domain.member.service;

import bobmukjaku.bobmukjakuDemo.global.jwt.service.JwtServiceImpl;
import bobmukjaku.bobmukjakuDemo.global.utility.RedisUtil;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        jwtService.deleteRefreshToken(username);
        redisUtil.setBlackList(accessToken, "accessToken", 60);
    }
}
