package com.seodong.portfolio.project;

import com.seodong.portfolio.common.dto.PageResponse;
import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.project.dto.ProjectDetailResponse;
import com.seodong.portfolio.project.dto.ProjectSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock ProjectRepository projectRepository;
    @InjectMocks ProjectService projectService;

    private Project sampleProject() {
        return Project.builder()
                .title("테스트 프로젝트")
                .summary("간략 소개")
                .description("설명")
                .githubUrl("https://github.com/test")
                .demoUrl("https://demo.test.com")
                .sortOrder(1)
                .build();
    }

    @Test
    @DisplayName("프로젝트 목록 조회 시 페이지 응답을 반환한다")
    void getProjects_returnsPaginatedResponse() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Project project = sampleProject();
        given(projectRepository.findAllOrdered(pageable))
                .willReturn(new PageImpl<>(List.of(project)));

        // when
        PageResponse<ProjectSummaryResponse> result = projectService.getProjects(pageable);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("테스트 프로젝트");
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.currentPage()).isEqualTo(0);
    }

    @Test
    @DisplayName("빈 목록일 때 빈 페이지 응답을 반환한다")
    void getProjects_empty_returnsEmptyPage() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        given(projectRepository.findAllOrdered(pageable))
                .willReturn(new PageImpl<>(List.of()));

        // when
        PageResponse<ProjectSummaryResponse> result = projectService.getProjects(pageable);

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하는 id로 단건 조회 시 상세 응답을 반환한다")
    void getProject_existing_returnsDetail() {
        // given
        Project project = sampleProject();
        given(projectRepository.findById(1L)).willReturn(Optional.of(project));

        // when
        ProjectDetailResponse response = projectService.getProject(1L);

        // then
        assertThat(response.title()).isEqualTo("테스트 프로젝트");
        assertThat(response.techStack()).isEmpty();
        assertThat(response.features()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 id로 단건 조회 시 예외가 발생한다")
    void getProject_notFound_throwsException() {
        // given
        given(projectRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectService.getProject(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("프로젝트");
    }
}
