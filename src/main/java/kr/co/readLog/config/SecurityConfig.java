package kr.co.readLog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import io.jsonwebtoken.io.IOException;
import java.util.Collections;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readLog.jwt.JWTFilter;
import kr.co.readLog.jwt.JWTUtil;
import kr.co.readLog.jwt.LoginFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;

	
	

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {

		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
	}

	// AuthenticationMager Bean 등록
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {

		return new BCryptPasswordEncoder();
	}
	
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationSuccessHandler successHandler) throws Exception {
		
		http
        .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:2001"));
                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L);

										configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                return configuration;
            }
        })));
		
		
		
		// csrf disable
		http.csrf((auth) -> auth.disable());

		// Form 로그인 방식 disable
		//http.formLogin((auth) -> auth.disable());

		// http basic 인증 방식 disable
		http.httpBasic((auth) -> auth.disable());

		// 경로별 인가 작업
		http.authorizeHttpRequests(
				(auth) -> auth.requestMatchers("/","/search" ,"login", "/home", "/join", "searchResult", "/css/**", "/js/**").permitAll()
						.requestMatchers("/search").hasRole("USER") ///Admin 페이지는 ADMIN이라는 role을 가진
						// 사용자만 가능
						.anyRequest().authenticated());
		
		http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
		
		//필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
        
        //http.formLogin(formLogin -> formLogin
                //.loginPage("/login") // 사용자 정의 로그인 페이지 URL (있는 경우)
                //.loginProcessingUrl("/perform_login") // 로그인 폼이 제출되어야 하는 URL
                //.successHandler(successHandler)); // 커스텀 인증 성공 핸들러
        
        // 세션 설정
		http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// session을 STATELESS 상태로 관리해주는것이 중요함!!

		return http.build();
	}
	
    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}
