package com.seodong.portfolio.project.admin;

import com.seodong.portfolio.common.dto.SimpleResponse;
import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.common.s3.S3Service;
import com.seodong.portfolio.project.MediaType;
import com.seodong.portfolio.project.ProjectMedia;
import com.seodong.portfolio.project.ProjectMediaRepository;
import com.seodong.portfolio.project.ProjectRepository;
import com.seodong.portfolio.project.dto.ProjectMediaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMediaController {

    private final S3Service s3Service;
    private final ProjectMediaRepository projectMediaRepository;
    private final ProjectRepository projectRepository;

    @PostMapping("/projects/{id}/media")
    public ResponseEntity<ProjectMediaResponse> upload(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "sortOrder", defaultValue = "0") int sortOrder) {

        var project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트를 찾을 수 없습니다."));

        String contentType = file.getContentType() != null ? file.getContentType() : "";
        MediaType mediaType = contentType.startsWith("video/") ? MediaType.VIDEO : MediaType.IMAGE;

        String url = s3Service.upload(file, "projects/" + id);

        ProjectMedia media = ProjectMedia.builder()
                .project(project)
                .url(url)
                .mediaType(mediaType)
                .sortOrder(sortOrder)
                .build();

        return ResponseEntity.status(201).body(ProjectMediaResponse.from(projectMediaRepository.save(media)));
    }

    @DeleteMapping("/media/{id}")
    public ResponseEntity<SimpleResponse> delete(@PathVariable Long id) {
        ProjectMedia media = projectMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("미디어를 찾을 수 없습니다."));
        s3Service.delete(media.getUrl());
        projectMediaRepository.delete(media);
        return ResponseEntity.ok(SimpleResponse.ok());
    }
}
