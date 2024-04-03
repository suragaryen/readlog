package kr.co.readLog.config;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException, java.io.IOException {
        if (exception instanceof LockedException) {
            // 사용자 계정이 잠겼을 때 사용자에게 메시지를 전송하고 다른 작업을 수행할 수 있습니다.
            response.sendRedirect(null);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
