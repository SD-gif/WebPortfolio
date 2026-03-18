package com.seodong.portfolio.skill.admin;

import com.seodong.portfolio.skill.dto.SkillCategoryRequest;
import com.seodong.portfolio.skill.dto.SkillCategoryResponse;
import com.seodong.portfolio.skill.dto.SkillItemResponse;
import com.seodong.portfolio.skill.dto.SkillRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/skills")
@RequiredArgsConstructor
public class AdminSkillController {

    private final AdminSkillService adminSkillService;

    @GetMapping
    public ResponseEntity<List<SkillCategoryResponse>> getCategories() {
        return ResponseEntity.ok(adminSkillService.getCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<SkillCategoryResponse> createCategory(@Valid @RequestBody SkillCategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminSkillService.createCategory(req));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<SkillCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody SkillCategoryRequest req) {
        return ResponseEntity.ok(adminSkillService.updateCategory(id, req));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminSkillService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories/{categoryId}/skills")
    public ResponseEntity<SkillItemResponse> addSkill(
            @PathVariable Long categoryId,
            @Valid @RequestBody SkillRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminSkillService.addSkill(categoryId, req));
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long skillId) {
        adminSkillService.deleteSkill(skillId);
        return ResponseEntity.noContent().build();
    }
}
