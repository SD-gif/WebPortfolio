package com.seodong.portfolio.security;

import com.seodong.portfolio.common.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtProviderTest {

    private static final String SECRET = "test-secret-key-minimum-32-characters-long!!";
    private static final long EXPIRATION_MS = 3_600_000L; // 1 hour

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secret", SECRET);
        ReflectionTestUtils.setField(jwtProvider, "expirationMs", EXPIRATION_MS);
        jwtProvider.init();
    }

    @Test
    @DisplayName("토큰 생성 후 username을 추출할 수 있다")
    void generateAndGetUsername() {
        String token = jwtProvider.generateToken("admin");
        assertThat(jwtProvider.getUsername(token)).isEqualTo("admin");
    }

    @Test
    @DisplayName("유효한 토큰 검증 시 true를 반환한다")
    void validateToken_valid_returnsTrue() {
        String token = jwtProvider.generateToken("admin");
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 토큰 검증 시 false를 반환한다")
    void validateToken_invalid_returnsFalse() {
        assertThat(jwtProvider.validateToken("invalid.jwt.token")).isFalse();
    }

    @Test
    @DisplayName("빈 문자열 토큰 검증 시 false를 반환한다")
    void validateToken_empty_returnsFalse() {
        assertThat(jwtProvider.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 false를 반환한다")
    void validateToken_expired_returnsFalse() {
        JwtProvider expiredProvider = new JwtProvider();
        ReflectionTestUtils.setField(expiredProvider, "secret", SECRET);
        ReflectionTestUtils.setField(expiredProvider, "expirationMs", -1000L);
        expiredProvider.init();

        String expiredToken = expiredProvider.generateToken("admin");
        assertThat(jwtProvider.validateToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("토큰에서 만료 시간을 추출할 수 있다")
    void getExpiration_returnsNonNullDate() {
        String token = jwtProvider.generateToken("admin");
        assertThat(jwtProvider.getExpiration(token)).isNotNull();
    }

    @Test
    @DisplayName("서로 다른 username으로 생성된 토큰은 각각 올바른 username을 반환한다")
    void generateToken_differentUsernames_returnsCorrectUsername() {
        String token1 = jwtProvider.generateToken("admin1");
        String token2 = jwtProvider.generateToken("admin2");

        assertThat(jwtProvider.getUsername(token1)).isEqualTo("admin1");
        assertThat(jwtProvider.getUsername(token2)).isEqualTo("admin2");
    }
}
