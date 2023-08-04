package bobmukjaku.bobmukjakuDemo.global.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    // 로그인 시, SecurityContextHandler에서 username 추출
    public static String getLoginUsername(){
        /*UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();*/

        Authentication loggedUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedUser.getName();
        return username;
    }
}
