package com.seodong.portfolio.education;

import com.seodong.portfolio.education.dto.EducationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;

    @Transactional(readOnly = true)
    public List<EducationResponse> getAll() {
        return educationRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(EducationResponse::from)
                .toList();
    }
}
