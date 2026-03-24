package com.seodong.portfolio.project.admin;

import com.seodong.portfolio.project.dto.ProjectDetailResponse;
import com.seodong.portfolio.project.dto.ProjectRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - 프로젝트")
@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final AdminProjectService adminProjectService;

    @PostMapping
    public ResponseEntity<ProjectDetailResponse> create(@Valid @RequestBody ProjectRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminProjectService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest req) {
        return ResponseEntity.ok(adminProjectService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminProjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
