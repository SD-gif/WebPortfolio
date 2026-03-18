package com.seodong.portfolio.education;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.education.admin.AdminEducationService;
import com.seodong.portfolio.education.dto.EducationRequest;
import com.seodong.portfolio.education.dto.EducationResponse;
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
class EducationServiceTest {

    @Mock EducationRepository educationRepository;

    @InjectMocks EducationService educationService;
    @InjectMocks AdminEducationService adminEducationService;

    private Education sampleEducation() {
        return Education.builder()
                .institution("OO 대학교").degree("컴퓨터공학 학사")
                .major("컴퓨터공학과").startDate("2018.03").endDate("2023.02")
                .sortOrder(1).build();
    }

    @Test
    @DisplayName("학력 목록 조회 시 전체 목록을 반환한다")
    void getAll_returnsAllEducations() {
        // given
        given(educationRepository.findAllByOrderBySortOrderAsc())
                .willReturn(List.of(sampleEducation()));

        // when
        List<EducationResponse> result = educationService.getAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).institution()).isEqualTo("OO 대학교");
        assertThat(result.get(0).degree()).isEqualTo("컴퓨터공학 학사");
    }

    @Test
    @DisplayName("학력이 없을 때 빈 목록을 반환한다")
    void getAll_empty_returnsEmptyList() {
        // given
        given(educationRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of());

        // when
        List<EducationResponse> result = educationService.getAll();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("학력 생성 시 저장된 결과를 반환한다")
    void create_validRequest_returnsSaved() {
        // given
        Education saved = sampleEducation();
        given(educationRepository.save(any())).willReturn(saved);
        EducationRequest req = new EducationRequest("OO 대학교", "컴퓨터공학 학사",
                "컴퓨터공학과", "2018.03", "2023.02", 1);

        // when
        EducationResponse response = adminEducationService.create(req);

        // then
        assertThat(response.institution()).isEqualTo("OO 대학교");
        assertThat(response.degree()).isEqualTo("컴퓨터공학 학사");
        then(educationRepository).should().save(any(Education.class));
    }

    @Test
    @DisplayName("학력 수정 시 변경된 내용을 반환한다")
    void update_existing_returnsUpdated() {
        // given
        Education education = sampleEducation();
        given(educationRepository.findById(1L)).willReturn(Optional.of(education));

        EducationRequest req = new EducationRequest("OO 대학원", "컴퓨터공학 석사",
                "AI 전공", "2023.03", "재학중", 1);

        // when
        EducationResponse response = adminEducationService.update(1L, req);

        // then
        assertThat(response.institution()).isEqualTo("OO 대학원");
        assertThat(response.degree()).isEqualTo("컴퓨터공학 석사");
    }

    @Test
    @DisplayName("존재하지 않는 학력 수정 시 예외가 발생한다")
    void update_notFound_throwsException() {
        // given
        given(educationRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminEducationService.update(999L,
                new EducationRequest("학교", "학사", "전공", null, null, 1)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("학력 삭제 시 정상 삭제된다")
    void delete_existing_deletesSuccessfully() {
        // given
        given(educationRepository.existsById(1L)).willReturn(true);

        // when
        adminEducationService.delete(1L);

        // then
        then(educationRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 학력 삭제 시 예외가 발생한다")
    void delete_notFound_throwsException() {
        // given
        given(educationRepository.existsById(999L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adminEducationService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        then(educationRepository).should(never()).deleteById(any());
    }
}
