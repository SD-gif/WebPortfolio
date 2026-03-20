package com.seodong.portfolio.certification.admin;

import com.seodong.portfolio.certification.dto.CertificationRequest;
import com.seodong.portfolio.certification.dto.CertificationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - 자격증")
@RestController
@RequestMapping("/api/admin/certifications")
@RequiredArgsConstructor
public class AdminCertificationController {

    private final AdminCertificationService adminCertificationService;

    @PostMapping
    public ResponseEntity<CertificationResponse> create(@Valid @RequestBody CertificationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCertificationService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CertificationRequest req) {
        return ResponseEntity.ok(adminCertificationService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminCertificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
