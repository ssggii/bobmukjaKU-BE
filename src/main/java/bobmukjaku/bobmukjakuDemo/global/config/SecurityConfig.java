package bobmukjaku.bobmukjakuDemo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /* 세부적인 보안 기능을 설정
       - 리소스(URL) 접근 권한 설정
       - 커스텀 로그인 페이지 지원
       - 인증 후 성공/실패 핸들링
       - 사용자 로그아웃
       - CSRF 공격으로 부터 보호
    */
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
/*
                .authorizeHttpRequests(
                        AuthorizationManager -> AuthorizationManager
                                .requestMatchers("/login", "/signUp", "/").permitAll());
*/

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /* 특정 요청들을 무시하고 싶을 때 사용 (보안 필터를 적용할 필요가 없는 리소스 설정) */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/"); // 로그인 페이지 인증 없이 접근 가능
    }
}
