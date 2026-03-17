package com.seodong.portfolio.skill;

import com.seodong.portfolio.skill.dto.SkillListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillCategoryRepository skillCategoryRepository;

    @Transactional(readOnly = true)
    public SkillListResponse getSkills() {
        List<SkillListResponse.CategoryItem> categories = skillCategoryRepository
                .findAllByOrderBySortOrderAsc()
                .stream()
                .map(SkillListResponse.CategoryItem::from)
                .toList();
        return new SkillListResponse(categories);
    }
}
