package com.seodong.portfolio.education.admin;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.education.Education;
import com.seodong.portfolio.education.EducationRepository;
import com.seodong.portfolio.education.dto.EducationRequest;
import com.seodong.portfolio.education.dto.EducationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminEducationService {

    private final EducationRepository educationRepository;

    @Transactional
    public EducationResponse create(EducationRequest req) {
        Education education = Education.builder()
                .institution(req.institution())
                .degree(req.degree())
                .major(req.major())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .sortOrder(req.sortOrder())
                .build();
        return EducationResponse.from(educationRepository.save(education));
    }

    @Transactional
    public EducationResponse update(Long id, EducationRequest req) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 학력 정보를 찾을 수 없습니다."));
        education.update(req.institution(), req.degree(), req.major(),
                req.startDate(), req.endDate(), req.sortOrder());
        return EducationResponse.from(education);
    }

    @Transactional
    public void delete(Long id) {
        if (!educationRepository.existsById(id)) {
            throw new ResourceNotFoundException("해당 학력 정보를 찾을 수 없습니다.");
        }
        educationRepository.deleteById(id);
    }
}
