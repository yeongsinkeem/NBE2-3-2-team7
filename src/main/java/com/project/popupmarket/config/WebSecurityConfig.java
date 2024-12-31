//package com.project.popupmarket.config;
//
//import com.project.popupmarket.service.userService.UserDetailService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
//
//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig {
//
//    private final UserDetailService userService;
//
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console())
//                .requestMatchers("/static/**");
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf
//                .ignoringRequestMatchers("/css/**", "/js/**", "/images/**", "/scripts/**")
//                .ignoringRequestMatchers("/h2-console/**")  // H2 콘솔용
//                )
//
//                .authorizeHttpRequests(auth -> auth
//                        // 누구나 접근 가능한 페이지
//                        .requestMatchers(antMatcher("/")).permitAll()
//                        .requestMatchers(antMatcher("/main")).permitAll()
//                        .requestMatchers(antMatcher("/popup/list")).permitAll()
//                        .requestMatchers(antMatcher("/rental/detail/**")).permitAll()
//                        .requestMatchers(antMatcher("/popup/detail/**")).permitAll()
//                        .requestMatchers(antMatcher("/register")).permitAll()
//                        .requestMatchers(("/login")).permitAll()
//                        .requestMatchers("/user").permitAll()
//                        // 로그인 필요한 페이지
//                        .requestMatchers(antMatcher("/rental/list")).authenticated()
//                        .requestMatchers(antMatcher("/mypage/**")).authenticated()
//                        .requestMatchers(antMatcher("/payment")).authenticated()
//                        .anyRequest().permitAll()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/main")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true)
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
//        return authenticationManagerBuilder.build();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}