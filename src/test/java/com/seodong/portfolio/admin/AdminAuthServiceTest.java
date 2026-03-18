package com.seodong.portfolio.admin;

import com.seodong.portfolio.admin.dto.LoginRequest;
import com.seodong.portfolio.admin.dto.LoginResponse;
import com.seodong.portfolio.common.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock JwtProvider jwtProvider;
    @InjectMocks AdminAuthService adminAuthService;

    @Test
    @DisplayName("올바른 자격증명으로 로그인 시 JWT 토큰을 반환한다")
    void login_validCredentials_returnsToken() {
        // given
        LoginRequest req = new LoginRequest("admin", "admin1234");
        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn("admin");
        given(authenticationManager.authenticate(any())).willReturn(auth);
        given(jwtProvider.generateToken("admin")).willReturn("mocked.jwt.token");

        // when
        LoginResponse response = adminAuthService.login(req);

        // then
        assertThat(response.token()).isEqualTo("mocked.jwt.token");
        then(authenticationManager).should().authenticate(
                new UsernamePasswordAuthenticationToken("admin", "admin1234")
        );
    }

    @Test
    @DisplayName("잘못된 자격증명으로 로그인 시 예외가 발생한다")
    void login_invalidCredentials_throwsException() {
        // given
        LoginRequest req = new LoginRequest("admin", "wrongPassword");
        given(authenticationManager.authenticate(any()))
                .willThrow(new BadCredentialsException("Bad credentials"));

        // when & then
        assertThatThrownBy(() -> adminAuthService.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }
}
