package com.seodong.portfolio.education.admin;

import com.seodong.portfolio.education.dto.EducationRequest;
import com.seodong.portfolio.education.dto.EducationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/educations")
@RequiredArgsConstructor
public class AdminEducationController {

    private final AdminEducationService adminEducationService;

    @PostMapping
    public ResponseEntity<EducationResponse> create(@Valid @RequestBody EducationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminEducationService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EducationRequest req) {
        return ResponseEntity.ok(adminEducationService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminEducationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
