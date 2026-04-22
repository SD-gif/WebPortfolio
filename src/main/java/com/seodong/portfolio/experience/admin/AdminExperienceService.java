package com.seodong.portfolio.experience.admin;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.experience.Experience;
import com.seodong.portfolio.experience.ExperienceRepository;
import com.seodong.portfolio.experience.dto.ExperienceRequest;
import com.seodong.portfolio.experience.dto.ExperienceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminExperienceService {

    private final ExperienceRepository experienceRepository;

    @Transactional
    public ExperienceResponse create(ExperienceRequest req) {
        Experience experience = Experience.builder()
                .icon(req.icon())
                .title(req.title())
                .summary(req.summary())
                .imageUrl(req.imageUrl())
                .sortOrder(req.sortOrder())
                .linkedProjectId(req.linkedProjectId())
                .build();
        experience.replaceSituation(req.situation());
        experience.replaceApproach(req.approach());
        experience.replaceLearned(req.learned());
        experience.replaceTechStacks(req.techStack());
        return ExperienceResponse.from(experienceRepository.save(experience));
    }

    @Transactional
    public ExperienceResponse update(Long id, ExperienceRequest req) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 경험을 찾을 수 없습니다."));
        experience.update(req.icon(), req.title(), req.summary(),
                req.imageUrl(), req.sortOrder(), req.linkedProjectId());
        experience.replaceSituation(req.situation());
        experience.replaceApproach(req.approach());
        experience.replaceLearned(req.learned());
        experience.replaceTechStacks(req.techStack());
        return ExperienceResponse.from(experience);
    }

    @Transactional
    public void delete(Long id) {
        if (!experienceRepository.existsById(id)) {
            throw new ResourceNotFoundException("해당 경험을 찾을 수 없습니다.");
        }
        experienceRepository.deleteById(id);
    }
}
