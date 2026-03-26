package com.seodong.portfolio.project.admin;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.project.Project;
import com.seodong.portfolio.project.ProjectFeature;
import com.seodong.portfolio.project.ProjectRepository;
import com.seodong.portfolio.project.ProjectTechStack;
import com.seodong.portfolio.project.dto.ProjectDetailResponse;
import com.seodong.portfolio.project.dto.ProjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectDetailResponse create(ProjectRequest req) {
        Project project = Project.builder()
                .title(req.title())
                .summary(req.summary())
                .description(req.description())
                .githubUrl(req.githubUrl())
                .demoUrl(req.demoUrl())
                .sortOrder(req.sortOrder())
                .build();

        addTechStacks(project, req.techStack());
        addFeatures(project, req.features());

        return ProjectDetailResponse.from(projectRepository.save(project));
    }

    @CacheEvict(cacheNames = "project", key = "#id")
    @Transactional
    public ProjectDetailResponse update(Long id, ProjectRequest req) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        project.update(req.title(), req.summary(), req.description(), req.githubUrl(), req.demoUrl(), req.sortOrder());

        project.getTechStacks().clear();
        addTechStacks(project, req.techStack());

        project.getFeatures().clear();
        addFeatures(project, req.features());

        return ProjectDetailResponse.from(projectRepository.save(project));
    }

    @CacheEvict(cacheNames = "project", key = "#id")
    @Transactional
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("해당 프로젝트를 찾을 수 없습니다.");
        }
        projectRepository.deleteById(id);
    }

    private void addTechStacks(Project project, List<String> techStack) {
        if (techStack == null) return;
        for (int i = 0; i < techStack.size(); i++) {
            project.getTechStacks().add(ProjectTechStack.builder()
                    .project(project)
                    .tech(techStack.get(i))
                    .sortOrder(i + 1)
                    .build());
        }
    }

    private void addFeatures(Project project, List<String> features) {
        if (features == null) return;
        for (int i = 0; i < features.size(); i++) {
            project.getFeatures().add(ProjectFeature.builder()
                    .project(project)
                    .feature(features.get(i))
                    .sortOrder(i + 1)
                    .build());
        }
    }
}
