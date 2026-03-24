package com.seodong.portfolio.exception;

import com.seodong.portfolio.common.exception.GlobalExceptionHandler;
import com.seodong.portfolio.common.exception.RateLimitException;
import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/bad-credentials")
        public void badCredentials() { throw new BadCredentialsException("Bad credentials"); }

        @GetMapping("/not-found")
        public void notFound() { throw new ResourceNotFoundException("리소스를 찾을 수 없습니다."); }

        @GetMapping("/rate-limit")
        public void rateLimit() { throw new RateLimitException("요청 한도 초과"); }

        @GetMapping("/server-error")
        public void serverError() { throw new RuntimeException("Unexpected error"); }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("BadCredentialsException 발생 시 401을 반환한다")
    void handleBadCredentials_returns401() throws Exception {
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("ResourceNotFoundException 발생 시 404를 반환한다")
    void handleNotFound_returns404() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("리소스를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("RateLimitException 발생 시 429를 반환한다")
    void handleRateLimit_returns429() throws Exception {
        mockMvc.perform(get("/test/rate-limit"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Too Many Requests"));
    }

    @Test
    @DisplayName("일반 Exception 발생 시 500을 반환한다")
    void handleGeneral_returns500() throws Exception {
        mockMvc.perform(get("/test/server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    @DisplayName("@Valid 검증 실패 시 400을 반환하고 errors 필드를 포함한다")
    void handleValidation_returns400WithErrors() throws Exception {
        @RestController
        @RequestMapping("/test-valid")
        class ValidController {
            record Req(@jakarta.validation.constraints.NotBlank String name) {}

            @PostMapping
            public void create(@org.springframework.web.bind.annotation.RequestBody
                               @jakarta.validation.Valid Req req) {}
        }

        MockMvc validMvc = MockMvcBuilders
                .standaloneSetup(new ValidController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        validMvc.perform(post("/test-valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());
    }
}
