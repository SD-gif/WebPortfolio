package com.seodong.portfolio.experience;

import com.seodong.portfolio.experience.dto.ExperienceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    @Transactional(readOnly = true)
    public List<ExperienceResponse> getAll() {
        return experienceRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(ExperienceResponse::from)
                .toList();
    }
}
