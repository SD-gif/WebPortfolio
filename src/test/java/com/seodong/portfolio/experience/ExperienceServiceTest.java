package com.seodong.portfolio.experience;

import com.seodong.portfolio.experience.dto.ExperienceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock ExperienceRepository experienceRepository;
    @InjectMocks ExperienceService experienceService;

    @Test
    @DisplayName("역량 목록 조회 시 정렬된 목록을 반환한다")
    void getAll_returnsSortedList() {
        Experience exp = Experience.builder()
                .title("비관적 락").summary("요약").situation("상황").approach("접근").learned("배운 점").sortOrder(1).build();
        given(experienceRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of(exp));

        List<ExperienceResponse> result = experienceService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("비관적 락");
    }

    @Test
    @DisplayName("역량 목록이 비어있으면 빈 리스트를 반환한다")
    void getAll_empty_returnsEmptyList() {
        given(experienceRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of());

        List<ExperienceResponse> result = experienceService.getAll();

        assertThat(result).isEmpty();
    }
}
