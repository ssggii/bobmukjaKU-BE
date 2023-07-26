package bobmukjaku.bobmukjakuDemo.global.config;

import bobmukjaku.bobmukjakuDemo.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    /* 특정 요청들을 무시하고 싶을 때 사용 (보안 필터를 적용할 필요가 없는 리소스 설정) */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/login", "/signUp", "/"); // 로그인, 회원가입 페이지 인증 없이 접근 가능
    }

    /* 세부적인 보안 기능을 설정 (authorization, authentication) */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .formLogin(FormLoginConfigurer::disable) // 1. formLogin 비활성화
                .httpBasic(HttpBasicConfigurer::disable) // 2. httpBasic 인증 비활성화 (특정 리소스에 접근 시 username과 password 물어봄)
                .csrf(CsrfConfigurer::disable) // 3. csrf 비활성화
                .sessionManagement( // 4. 세션 Stateless로 유지
                        httpSecuritySessionManagementConfigurer
                                -> httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
/*                .authorizeHttpRequests(
                        AuthorizationManager -> AuthorizationManager
                                .requestMatchers("/login", "/signUp", "/").permitAll()
                                .anyRequest().authenticated());*/

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){ // 1. PasswordEncoder 등록
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {// 2. AuthenticationManager 등록
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();// DaoAuthenticationProvider 사용
        provider.setPasswordEncoder(passwordEncoder());// PasswordEncoder로는 PasswordEncoderFactories.createDelegatingPasswordEncoder() 사용
        //provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter(){
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        //jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        //jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());//변경
        return jsonUsernamePasswordLoginFilter;
    }
}
