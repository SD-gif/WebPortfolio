package com.seodong.portfolio.skill;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.skill.admin.AdminSkillService;
import com.seodong.portfolio.skill.dto.SkillCategoryRequest;
import com.seodong.portfolio.skill.dto.SkillCategoryResponse;
import com.seodong.portfolio.skill.dto.SkillItemResponse;
import com.seodong.portfolio.skill.dto.SkillListResponse;
import com.seodong.portfolio.skill.dto.SkillRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock SkillCategoryRepository categoryRepository;
    @Mock SkillRepository skillRepository;

    @InjectMocks SkillService skillService;
    @InjectMocks AdminSkillService adminSkillService;

    private SkillCategory sampleCategory(Long id, String name) {
        return SkillCategory.builder().name(name).sortOrder(1).build();
    }

    @Test
    @DisplayName("스킬 목록 조회 시 카테고리와 스킬을 반환한다")
    void getSkills_returnsCategories() {
        // given
        SkillCategory backend = sampleCategory(1L, "Backend");
        given(categoryRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of(backend));

        // when
        SkillListResponse response = skillService.getSkills();

        // then
        assertThat(response.categories()).hasSize(1);
        assertThat(response.categories().get(0).category()).isEqualTo("Backend");
    }

    @Test
    @DisplayName("스킬 카테고리 목록이 없을 때 빈 리스트를 반환한다")
    void getSkills_emptyList_returnsEmpty() {
        // given
        given(categoryRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of());

        // when
        SkillListResponse response = skillService.getSkills();

        // then
        assertThat(response.categories()).isEmpty();
    }

    @Test
    @DisplayName("카테고리 생성 시 저장된 카테고리를 반환한다")
    void createCategory_validRequest_returnsSaved() {
        // given
        SkillCategoryRequest req = new SkillCategoryRequest("Frontend", 2);
        SkillCategory saved = SkillCategory.builder().name("Frontend").sortOrder(2).build();
        given(categoryRepository.save(any())).willReturn(saved);

        // when
        SkillCategoryResponse response = adminSkillService.createCategory(req);

        // then
        assertThat(response.name()).isEqualTo("Frontend");
        assertThat(response.sortOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("카테고리 수정 시 변경된 내용을 반환한다")
    void updateCategory_existing_returnsUpdated() {
        // given
        SkillCategory category = SkillCategory.builder().name("Old").sortOrder(1).build();
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        // when
        SkillCategoryResponse response = adminSkillService.updateCategory(1L,
                new SkillCategoryRequest("New", 3));

        // then
        assertThat(response.name()).isEqualTo("New");
        assertThat(response.sortOrder()).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 수정 시 예외가 발생한다")
    void updateCategory_notFound_throwsException() {
        // given
        given(categoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminSkillService.updateCategory(999L,
                new SkillCategoryRequest("X", 1)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("카테고리 삭제 시 정상 삭제된다")
    void deleteCategory_existing_deletesSuccessfully() {
        // given
        given(categoryRepository.existsById(1L)).willReturn(true);

        // when
        adminSkillService.deleteCategory(1L);

        // then
        then(categoryRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 삭제 시 예외가 발생한다")
    void deleteCategory_notFound_throwsException() {
        // given
        given(categoryRepository.existsById(999L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adminSkillService.deleteCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        then(categoryRepository).should(never()).deleteById(any());
    }

    @Test
    @DisplayName("스킬 추가 시 카테고리에 스킬이 추가된다")
    void addSkill_existingCategory_returnsSkill() {
        // given
        SkillCategory category = SkillCategory.builder().name("Backend").sortOrder(1).build();
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        // when
        SkillItemResponse response = adminSkillService.addSkill(1L, new SkillRequest("Java", 1));

        // then
        assertThat(response.name()).isEqualTo("Java");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리에 스킬 추가 시 예외가 발생한다")
    void addSkill_categoryNotFound_throwsException() {
        // given
        given(categoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminSkillService.addSkill(999L, new SkillRequest("Java", 1)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("스킬 삭제 시 정상 삭제된다")
    void deleteSkill_existing_deletesSuccessfully() {
        // given
        given(skillRepository.existsById(1L)).willReturn(true);

        // when
        adminSkillService.deleteSkill(1L);

        // then
        then(skillRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 스킬 삭제 시 예외가 발생한다")
    void deleteSkill_notFound_throwsException() {
        // given
        given(skillRepository.existsById(999L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adminSkillService.deleteSkill(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("관리자 스킬 카테고리 목록 조회 시 SkillCategoryResponse 목록을 반환한다")
    void getCategories_returnsSkillCategoryResponses() {
        // given
        SkillCategory category = SkillCategory.builder().name("Backend").sortOrder(1).build();
        given(categoryRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of(category));

        // when
        List<SkillCategoryResponse> result = adminSkillService.getCategories();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Backend");
    }

    @Test
    @DisplayName("스킬이 포함된 카테고리 조회 시 스킬 목록도 반환한다")
    void getSkills_withSkills_returnsSkillsInCategory() {
        // given
        SkillCategory category = SkillCategory.builder().name("Backend").sortOrder(1).build();
        category.addSkill("Java", 1);
        category.addSkill("Spring", 2);
        given(categoryRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of(category));

        // when
        SkillListResponse response = skillService.getSkills();

        // then
        assertThat(response.categories().get(0).skills()).hasSize(2);
    }
}
