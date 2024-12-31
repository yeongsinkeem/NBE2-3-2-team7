package com.project.popupmarket.config;

import com.project.popupmarket.config.handler.BaseAuthenticationSuccessHandler;
import com.project.popupmarket.config.handler.FormLoginSuccessHandler;
import com.project.popupmarket.config.jwt.TokenAuthenticationFilter;
import com.project.popupmarket.config.jwt.TokenProvider;
import com.project.popupmarket.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.project.popupmarket.config.handler.OAuth2SuccessHandler;
import com.project.popupmarket.service.userService.OAuth2UserCustomService;
import com.project.popupmarket.repository.JwtTokenRepository;
import com.project.popupmarket.service.userService.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final JwtTokenRepository jwtTokenRepository;
    private final UserService userService;

    // WebSecurityCustomizer 빈 등록
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }

    // SecurityFilterChain 빈 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWT 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // API 요청에 대한 권한 설정
        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        // API 엔드포인트 설정
                        .requestMatchers("/api/token").permitAll()
                        .requestMatchers("/api/**").authenticated()

                        // 누구나 접근 가능한 페이지
                        .requestMatchers(antMatcher("/")).permitAll()
                        .requestMatchers(antMatcher("/main")).permitAll()
                        .requestMatchers(antMatcher("/popup/list")).permitAll()
                        .requestMatchers(antMatcher("/rental/detail/**")).permitAll()
                        .requestMatchers(antMatcher("/popup/detail/**")).permitAll()
                        .requestMatchers(antMatcher("/register/**")).permitAll()
                        .requestMatchers(antMatcher("/login/**")).permitAll()
                        .requestMatchers("/signup").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/login/oauth2/**").permitAll()

                        // 로그인 필요한 페이지
                        .requestMatchers(antMatcher("/rental/list")).authenticated()
                        .requestMatchers(antMatcher("/mypage/**")).authenticated()
                        .requestMatchers(antMatcher("/payment")).authenticated()

                        .anyRequest().permitAll()
        );

        // 폼 로그인 설정
        http.formLogin(formLogin -> formLogin
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/api/login")
                .successHandler(formLoginSuccessHandler())
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"" + exception.getMessage() + "\"}");
                })
                .permitAll()
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2Login -> oauth2Login
                .loginPage("/login")
                .authorizationEndpoint(endpoint ->
                        endpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                )
                .successHandler(oAuth2SuccessHandler())
                .userInfoEndpoint(endpoint ->
                        endpoint.userService(oAuth2UserCustomService)
                )
        );

        // 로그아웃 설정
        http.logout(logout -> logout
                .logoutUrl("/api/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                })
                .invalidateHttpSession(true)
                .deleteCookies(BaseAuthenticationSuccessHandler.JWT_TOKEN_COOKIE_NAME)
        );


        // 예외 처리 핸들링
        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")));

        return http.build();
    }

    // OAuth2 로그인 성공 핸들러 추가
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                jwtTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    // 폼 로그인 성공 핸들러 추가
    @Bean
    public FormLoginSuccessHandler formLoginSuccessHandler() {
        return new FormLoginSuccessHandler(tokenProvider, jwtTokenRepository);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    // OAuth2AuthorizationRequestBasedOnCookieRepository 빈 등록
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    // BCryptPasswordEncoder 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}