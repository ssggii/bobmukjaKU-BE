package bobmukjaku.bobmukjakuDemo.global.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login"; // /oauth2/login/* 요청을 처리할 예정

    private static final String HTTP_METHOD = "POST";

    private static final String CONTENT_TYPE = "application/json";
    private static final String CONTENT_TYPE2 = "application/json; charset=UTF-8"; // 앱에서 받는 content-type이 이 경우일 수 있음

    private final ObjectMapper objectMapper;

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";


    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD); // -> /login, POST로 온 요청에 매칭

    public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {

        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);   // GET으로 온 요청을 처리하기 위해 설정
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if(request.getContentType() == null || !(request.getContentType().equals(CONTENT_TYPE) || request.getContentType().equals(CONTENT_TYPE2))) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

        String username = usernamePasswordMap.get(USERNAME_KEY);
        String password = usernamePasswordMap.get(PASSWORD_KEY);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);//principal 과 credentials 전달

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
