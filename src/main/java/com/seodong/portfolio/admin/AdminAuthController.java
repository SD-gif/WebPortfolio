package com.seodong.portfolio.admin;

import com.seodong.portfolio.admin.dto.LoginRequest;
import com.seodong.portfolio.common.dto.SimpleResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin - 인증")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<SimpleResponse> login(@RequestBody LoginRequest req,
                                                HttpServletResponse response) {
        String token = adminAuthService.login(req);
        Cookie cookie = new Cookie("admin_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 24시간
        response.addCookie(cookie);
        return ResponseEntity.ok(SimpleResponse.ok());
    }

    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("admin_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(SimpleResponse.ok());
    }
}
