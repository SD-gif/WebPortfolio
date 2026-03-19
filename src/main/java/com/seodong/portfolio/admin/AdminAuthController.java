package com.seodong.portfolio.admin;

import com.seodong.portfolio.admin.dto.LoginRequest;
import com.seodong.portfolio.admin.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin - 인증")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(adminAuthService.login(req));
    }
}
