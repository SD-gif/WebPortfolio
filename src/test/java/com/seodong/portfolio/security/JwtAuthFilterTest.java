package com.seodong.portfolio.security;

import com.seodong.portfolio.common.security.CustomUserDetailsService;
import com.seodong.portfolio.common.security.JwtAuthFilter;
import com.seodong.portfolio.common.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock JwtProvider jwtProvider;
    @Mock CustomUserDetailsService userDetailsService;

    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test-auth")
        public String test() { return "ok"; }
    }

    @BeforeEach
    void setUp() {
        JwtAuthFilter filter = new JwtAuthFilter(jwtProvider, userDetailsService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .addFilter(filter)
                .build();
    }

    @Test
    @DisplayName("유효한 Bearer 토큰이 있을 때 요청을 통과시킨다")
    void validToken_passesRequest() throws Exception {
        String token = "valid.jwt.token";
        given(jwtProvider.validateToken(token)).willReturn(true);
        given(jwtProvider.getUsername(token)).willReturn("admin");
        given(userDetailsService.loadUserByUsername("admin"))
                .willReturn(new User("admin", "pass",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        mockMvc.perform(get("/test-auth").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        then(jwtProvider).should().validateToken(token);
        then(jwtProvider).should().getUsername(token);
        then(userDetailsService).should().loadUserByUsername("admin");
    }

    @Test
    @DisplayName("Authorization 헤더가 없을 때 인증 없이 요청을 통과시킨다")
    void noHeader_passesWithoutAuth() throws Exception {
        mockMvc.perform(get("/test-auth"))
                .andExpect(status().isOk());

        then(jwtProvider).should(never()).validateToken(any());
    }

    @Test
    @DisplayName("Bearer 접두사 없는 헤더는 무시한다")
    void noBearerPrefix_ignoresHeader() throws Exception {
        mockMvc.perform(get("/test-auth").header("Authorization", "Token sometoken"))
                .andExpect(status().isOk());

        then(jwtProvider).should(never()).validateToken(any());
    }

    @Test
    @DisplayName("유효하지 않은 토큰일 때 인증 없이 요청을 통과시킨다")
    void invalidToken_passesWithoutAuth() throws Exception {
        given(jwtProvider.validateToken("bad.token")).willReturn(false);

        mockMvc.perform(get("/test-auth").header("Authorization", "Bearer bad.token"))
                .andExpect(status().isOk());

        then(userDetailsService).should(never()).loadUserByUsername(any());
    }
}
