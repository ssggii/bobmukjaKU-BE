package bobmukjaku.bobmukjakuDemo.global.config;

import bobmukjaku.bobmukjakuDemo.domain.member.Role;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.service.LoginService;
import bobmukjaku.bobmukjakuDemo.global.jwt.filter.JwtAuthenticationProcessingFilter;
import bobmukjaku.bobmukjakuDemo.global.jwt.service.JwtService;
import bobmukjaku.bobmukjakuDemo.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import bobmukjaku.bobmukjakuDemo.global.login.handler.LoginFailureHandler;
import bobmukjaku.bobmukjakuDemo.global.login.handler.LoginSuccessJWTProvideHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
import org.springframework.security.web.authentication.logout.LogoutFilter;

import static org.hibernate.cfg.AvailableSettings.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final LoginService loginService;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    private static final String[] WHITE_LIST = {
            "/", "/login**", "/signUp**", "/check/nickname"
    };

    /* 특정 url 요청 무시 */
/*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(WHITE_LIST);
    }
*/

    /* 세부적인 보안 기능 설정 (authorization, authentication) */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .formLogin(FormLoginConfigurer::disable) // 1. formLogin 비활성화
                .httpBasic(HttpBasicConfigurer::disable) // 2. httpBasic 인증 비활성화 (특정 리소스에 접근 시 username과 password 물어봄)
                .csrf(CsrfConfigurer::disable) // 3. csrf 비활성화
                .sessionManagement( // 4. 세션 Stateless로 유지
                        httpSecuritySessionManagementConfigurer
                                -> httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(WHITE_LIST).permitAll()
                                .requestMatchers("/members").permitAll()
                                .requestMatchers("/member/info", "/member/info/*").permitAll()
                                .anyRequest().authenticated());

        http.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){ // 1. PasswordEncoder 등록
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJWTProvideHandler(jwtService, memberRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager() {// 2. AuthenticationManager 등록
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();// DaoAuthenticationProvider 사용
        provider.setPasswordEncoder(passwordEncoder());// PasswordEncoder로는 PasswordEncoderFactories.createDelegatingPasswordEncoder() 사용
        provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter(){
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());//변경
        return jsonUsernamePasswordLoginFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(){
        JwtAuthenticationProcessingFilter jsonUsernamePasswordLoginFilter = new JwtAuthenticationProcessingFilter(jwtService, memberRepository);

        return jsonUsernamePasswordLoginFilter;
    }
}
