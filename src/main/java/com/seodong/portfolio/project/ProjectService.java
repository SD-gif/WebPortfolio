package com.seodong.portfolio.project;

import com.seodong.portfolio.common.dto.PageResponse;
import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.project.dto.ProjectDetailResponse;
import com.seodong.portfolio.project.dto.ProjectSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public PageResponse<ProjectSummaryResponse> getProjects(Pageable pageable) {
        return PageResponse.from(
                projectRepository.findAllOrdered(pageable).map(ProjectSummaryResponse::from)
        );
    }

    @Cacheable(cacheNames = "project", key = "#id")
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProject(Long id) {
        return projectRepository.findById(id)
                .map(ProjectDetailResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("해당 프로젝트를 찾을 수 없습니다."));
    }
}
