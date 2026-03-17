package com.seodong.portfolio.profile.admin;

import com.seodong.portfolio.profile.dto.ProfileRequest;
import com.seodong.portfolio.profile.dto.ProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    @PutMapping
    public ResponseEntity<ProfileResponse> update(@Valid @RequestBody ProfileRequest req) {
        return ResponseEntity.ok(adminProfileService.update(req));
    }
}
