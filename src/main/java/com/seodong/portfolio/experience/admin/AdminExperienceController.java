package com.seodong.portfolio.experience.admin;

import com.seodong.portfolio.experience.dto.ExperienceRequest;
import com.seodong.portfolio.experience.dto.ExperienceResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - 경험")
@RestController
@RequestMapping("/api/admin/experiences")
@RequiredArgsConstructor
public class AdminExperienceController {

    private final AdminExperienceService adminExperienceService;

    @PostMapping
    public ResponseEntity<ExperienceResponse> create(@Valid @RequestBody ExperienceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminExperienceService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExperienceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ExperienceRequest req) {
        return ResponseEntity.ok(adminExperienceService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminExperienceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
