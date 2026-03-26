package com.seodong.portfolio.project;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.project.admin.AdminProjectService;
import com.seodong.portfolio.project.dto.ProjectDetailResponse;
import com.seodong.portfolio.project.dto.ProjectRequest;
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
class AdminProjectServiceTest {

    @Mock ProjectRepository projectRepository;
    @InjectMocks AdminProjectService adminProjectService;

    private ProjectRequest sampleRequest() {
        return new ProjectRequest("포트폴리오", "간략 소개", "설명", "https://github.com/test", null, 1,
                List.of("Java", "Spring"), List.of("기능1"));
    }

    @Test
    @DisplayName("프로젝트 생성 시 저장된 프로젝트를 반환한다")
    void create_validRequest_returnsSavedProject() {
        // given
        ProjectRequest req = sampleRequest();
        Project saved = Project.builder()
                .title(req.title()).summary(req.summary()).description(req.description())
                .githubUrl(req.githubUrl()).sortOrder(req.sortOrder())
                .build();
        given(projectRepository.save(any())).willReturn(saved);

        // when
        ProjectDetailResponse response = adminProjectService.create(req);

        // then
        assertThat(response.title()).isEqualTo("포트폴리오");
        then(projectRepository).should().save(any(Project.class));
    }

    @Test
    @DisplayName("존재하는 프로젝트 수정 시 수정된 결과를 반환한다")
    void update_existingProject_returnsUpdated() {
        // given
        Project existing = Project.builder()
                .title("기존 제목").summary("기존 소개").description("기존 설명").sortOrder(1).build();
        given(projectRepository.findById(1L)).willReturn(Optional.of(existing));
        given(projectRepository.save(any())).willReturn(existing);

        ProjectRequest req = new ProjectRequest("변경 제목", "변경 소개", "변경 설명", null, null, 2,
                List.of("Kotlin"), List.of("기능A"));

        // when
        ProjectDetailResponse response = adminProjectService.update(1L, req);

        // then
        assertThat(response.title()).isEqualTo("변경 제목");
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 수정 시 예외가 발생한다")
    void update_nonExistingProject_throwsException() {
        // given
        given(projectRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProjectService.update(999L, sampleRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("존재하는 프로젝트 삭제 시 정상 삭제된다")
    void delete_existingProject_deletesSuccessfully() {
        // given
        given(projectRepository.existsById(1L)).willReturn(true);

        // when
        adminProjectService.delete(1L);

        // then
        then(projectRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 삭제 시 예외가 발생한다")
    void delete_nonExistingProject_throwsException() {
        // given
        given(projectRepository.existsById(999L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adminProjectService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        then(projectRepository).should(never()).deleteById(any());
    }
}
