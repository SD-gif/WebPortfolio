package com.seodong.portfolio.certification;

import com.seodong.portfolio.certification.dto.CertificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;

    @Transactional(readOnly = true)
    public List<CertificationResponse> getAll() {
        return certificationRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(CertificationResponse::from)
                .toList();
    }
}
