package kr.co.readLog.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

	@Component
	public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	    @Override
	    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, java.io.IOException {
	        // 로그인 성공 후 리다이렉트할 URL 설정
	        String targetUrl = "/search";

	        redirectStrategy.sendRedirect(request, response, targetUrl);
	    }
	}

