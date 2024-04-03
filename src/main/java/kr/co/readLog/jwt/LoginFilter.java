package kr.co.readLog.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Repository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readLog.Repository.CustomUserDetails;
import kr.co.readLog.dto.JwtTokenDto;

public class LoginFilter extends UsernamePasswordAuthenticationFilter { // 사용자 인증 폼 제출을 처리한다. 
																		// 로그인 폼은 이 필터에게 사용자 이름과 비밀번호를 매개변수로 제공해야 함
																		// 기본 매개 변수는 SPRING_SECURITY_FORM_USERNAME_KEY와 SPRING_SECURITY_FORM_PASSWORD_KEY 라는 정적 필드에 포함 되어 있음.
																		// usernameParameter와 passwordParameter 속성을 설정하여 변경할 수도 있다.

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil; //SecurityConfig에서도 생성자 주입을 해줘야 한다(매개변수로 사용하기 때문에)
	

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		// 클라이언트 요청에서 username, password 추출
	    String username = obtainUsername(request);
        String password = obtainPassword(request);
        
		System.out.println(username);
		System.out.println(password);

		// 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

		// token에 담은 검증을 위한 AuthenticationManager로 전달
		return authenticationManager.authenticate(authToken);
	}

	// 로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException {
		
		
		JwtTokenDto jwtTokenDto = new JwtTokenDto();
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		String username = customUserDetails.getUsername();


		
		 Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
	        GrantedAuthority auth = iterator.next();

	        String role = auth.getAuthority();
	        long expireTimeMs = 1000 * 60 * 60; 
	        String token = jwtUtil.createJwt(username, role, expireTimeMs);
			String Username = jwtUtil.getUsername(token);
	        
	        //String refreshToken = jwtUtil.createRefreshToken(username, 60*60*10L);
			 jwtTokenDto.setAccessToken(token);
	        
			response.addHeader("Authorization", "Bearer " + token);
	        response.sendRedirect("/search");
	        
	        System.out.println(Username + "님 로그인 성공" + role);
		
		//jwtTokenDto.setRefreshToken(refreshToken);
		
		//response.addHeader(nickname, refreshToken);
		System.out.println(token);
		
		//HTTP 인증 방식은 RFC 7235 정의에 따라 아래 인증 헤더 형태를 가져야 한다.
		//Authorization: 타입 인증토큰

		//예시
		//Authorization: Bearer 인증토큰string

	}

	// 로그인 실패시 실행하는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) {
		
		//로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
        System.out.println(failed);

	}
}
