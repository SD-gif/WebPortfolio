package com.seodong.portfolio.skill.admin;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.skill.SkillCategory;
import com.seodong.portfolio.skill.SkillCategoryRepository;
import com.seodong.portfolio.skill.SkillRepository;
import com.seodong.portfolio.skill.dto.SkillCategoryRequest;
import com.seodong.portfolio.skill.dto.SkillCategoryResponse;
import com.seodong.portfolio.skill.dto.SkillItemResponse;
import com.seodong.portfolio.skill.dto.SkillRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSkillService {

    private final SkillCategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<SkillCategoryResponse> getCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(SkillCategoryResponse::from)
                .toList();
    }

    @Transactional
    public SkillCategoryResponse createCategory(SkillCategoryRequest req) {
        SkillCategory category = SkillCategory.builder()
                .name(req.name())
                .sortOrder(req.sortOrder())
                .build();
        return SkillCategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public SkillCategoryResponse updateCategory(Long id, SkillCategoryRequest req) {
        SkillCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 카테고리를 찾을 수 없습니다."));
        category.update(req.name(), req.sortOrder());
        return SkillCategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("해당 카테고리를 찾을 수 없습니다.");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public SkillItemResponse addSkill(Long categoryId, SkillRequest req) {
        SkillCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 카테고리를 찾을 수 없습니다."));
        return SkillItemResponse.from(category.addSkill(req.name(), req.sortOrder()));
    }

    @Transactional
    public void deleteSkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new ResourceNotFoundException("해당 스킬을 찾을 수 없습니다.");
        }
        skillRepository.deleteById(skillId);
    }
}
