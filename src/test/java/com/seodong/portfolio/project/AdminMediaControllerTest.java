package com.seodong.portfolio.project;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.common.s3.S3Service;
import com.seodong.portfolio.project.admin.AdminMediaController;
import com.seodong.portfolio.project.dto.ProjectMediaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMediaControllerTest {

    @Mock S3Service s3Service;
    @Mock ProjectMediaRepository projectMediaRepository;
    @Mock ProjectRepository projectRepository;
    @Mock CacheManager cacheManager;
    @InjectMocks AdminMediaController adminMediaController;

    @Test
    @DisplayName("이미지 업로드 시 201과 미디어 응답을 반환한다")
    void upload_image_returns201() {
        Project project = Project.builder().title("테스트").build();
        given(projectRepository.findById(1L)).willReturn(Optional.of(project));
        given(s3Service.upload(any(), eq("projects/1"))).willReturn("https://s3.com/img.png");

        ProjectMedia saved = ProjectMedia.builder()
                .project(project).url("https://s3.com/img.png")
                .mediaType(MediaType.IMAGE).sortOrder(0).build();
        given(projectMediaRepository.save(any())).willReturn(saved);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "data".getBytes());

        ResponseEntity<ProjectMediaResponse> response = adminMediaController.upload(1L, file, 0);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().mediaType()).isEqualTo("IMAGE");
    }

    @Test
    @DisplayName("동영상 업로드 시 VIDEO 타입으로 저장된다")
    void upload_video_savesAsVideo() {
        Project project = Project.builder().title("테스트").build();
        given(projectRepository.findById(1L)).willReturn(Optional.of(project));
        given(s3Service.upload(any(), eq("projects/1"))).willReturn("https://s3.com/vid.mp4");

        ProjectMedia saved = ProjectMedia.builder()
                .project(project).url("https://s3.com/vid.mp4")
                .mediaType(MediaType.VIDEO).sortOrder(0).build();
        given(projectMediaRepository.save(any())).willReturn(saved);

        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "data".getBytes());

        ResponseEntity<ProjectMediaResponse> response = adminMediaController.upload(1L, file, 0);

        assertThat(response.getBody().mediaType()).isEqualTo("VIDEO");
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트에 업로드 시 예외가 발생한다")
    void upload_projectNotFound_throwsException() {
        given(projectRepository.findById(999L)).willReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "data".getBytes());

        assertThatThrownBy(() -> adminMediaController.upload(999L, file, 0))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("미디어 삭제 시 S3와 DB에서 삭제되고 캐시가 evict된다")
    void delete_existing_deletesAndEvictsCache() {
        Project project = Project.builder().title("테스트").build();
        ProjectMedia media = ProjectMedia.builder()
                .project(project).url("https://s3.com/img.png")
                .mediaType(MediaType.IMAGE).build();
        given(projectMediaRepository.findById(1L)).willReturn(Optional.of(media));

        Cache cache = mock(Cache.class);
        given(cacheManager.getCache("project")).willReturn(cache);

        adminMediaController.delete(1L);

        then(s3Service).should().delete("https://s3.com/img.png");
        then(projectMediaRepository).should().delete(media);
    }

    @Test
    @DisplayName("존재하지 않는 미디어 삭제 시 예외가 발생한다")
    void delete_notFound_throwsException() {
        given(projectMediaRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminMediaController.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
