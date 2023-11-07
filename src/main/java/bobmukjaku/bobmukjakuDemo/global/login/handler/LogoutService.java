package bobmukjaku.bobmukjakuDemo.global.login.handler;

import bobmukjaku.bobmukjakuDemo.global.utility.RedisUtil;
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
    public void logout() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if (redisUtil.get("loginID:" + username) != null) {
            redisUtil.delete("loginID:" + username); // Redisfi 에서 토큰 삭제
        }
    }
}
