package com.seodong.portfolio.experience;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.experience.admin.AdminExperienceService;
import com.seodong.portfolio.experience.dto.ExperienceRequest;
import com.seodong.portfolio.experience.dto.ExperienceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AdminExperienceServiceTest {

    @Mock ExperienceRepository experienceRepository;
    @InjectMocks AdminExperienceService adminExperienceService;

    private ExperienceRequest sampleRequest() {
        return new ExperienceRequest(
                null, "비관적 락", "요약",
                List.of("상황1 : 문제 설명"),
                List.of("접근1 : 해결책 설명"),
                List.of("배운 점1 : 교훈 설명"),
                null, List.of("JPA", "PostgreSQL"), 1, null);
    }

    @Test
    @DisplayName("역량 생성 시 저장된 역량을 반환한다")
    void create_validRequest_returnsSaved() {
        ExperienceRequest req = sampleRequest();
        Experience saved = Experience.builder()
                .title(req.title()).summary(req.summary()).sortOrder(req.sortOrder())
                .build();
        saved.replaceSituation(req.situation());
        saved.replaceApproach(req.approach());
        saved.replaceLearned(req.learned());
        given(experienceRepository.save(any())).willReturn(saved);

        ExperienceResponse response = adminExperienceService.create(req);

        assertThat(response.title()).isEqualTo("비관적 락");
        assertThat(response.situation()).containsExactly("상황1 : 문제 설명");
        then(experienceRepository).should().save(any(Experience.class));
    }

    @Test
    @DisplayName("존재하는 역량 수정 시 수정된 결과를 반환한다")
    void update_existing_returnsUpdated() {
        Experience existing = Experience.builder()
                .title("기존").summary("기존 요약").sortOrder(1).build();
        existing.replaceSituation(List.of("기존 상황"));
        existing.replaceApproach(List.of("기존 접근"));
        existing.replaceLearned(List.of("기존 배운 점"));
        given(experienceRepository.findById(1L)).willReturn(Optional.of(existing));

        ExperienceResponse response = adminExperienceService.update(1L, sampleRequest());

        assertThat(response.title()).isEqualTo("비관적 락");
        assertThat(response.situation()).containsExactly("상황1 : 문제 설명");
    }

    @Test
    @DisplayName("존재하지 않는 역량 수정 시 예외가 발생한다")
    void update_notFound_throwsException() {
        given(experienceRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminExperienceService.update(999L, sampleRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("존재하는 역량 삭제 시 정상 삭제된다")
    void delete_existing_deletesSuccessfully() {
        given(experienceRepository.existsById(1L)).willReturn(true);

        adminExperienceService.delete(1L);

        then(experienceRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 역량 삭제 시 예외가 발생한다")
    void delete_notFound_throwsException() {
        given(experienceRepository.existsById(999L)).willReturn(false);

        assertThatThrownBy(() -> adminExperienceService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        then(experienceRepository).should(never()).deleteById(any());
    }
}
