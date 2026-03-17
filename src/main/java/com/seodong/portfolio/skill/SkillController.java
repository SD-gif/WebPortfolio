package com.seodong.portfolio.skill;

import com.seodong.portfolio.skill.dto.SkillListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<SkillListResponse> getSkills() {
        return ResponseEntity.ok(skillService.getSkills());
    }
}
