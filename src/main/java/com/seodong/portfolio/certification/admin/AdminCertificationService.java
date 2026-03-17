package com.seodong.portfolio.certification.admin;

import com.seodong.portfolio.certification.Certification;
import com.seodong.portfolio.certification.CertificationRepository;
import com.seodong.portfolio.certification.dto.CertificationRequest;
import com.seodong.portfolio.certification.dto.CertificationResponse;
import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCertificationService {

    private final CertificationRepository certificationRepository;

    @Transactional
    public CertificationResponse create(CertificationRequest req) {
        Certification cert = Certification.builder()
                .name(req.name())
                .issuer(req.issuer())
                .acquiredDate(req.acquiredDate())
                .type(req.type())
                .sortOrder(req.sortOrder())
                .build();
        return CertificationResponse.from(certificationRepository.save(cert));
    }

    @Transactional
    public CertificationResponse update(Long id, CertificationRequest req) {
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 자격증/수상 정보를 찾을 수 없습니다."));
        cert.update(req.name(), req.issuer(), req.acquiredDate(), req.type(), req.sortOrder());
        return CertificationResponse.from(cert);
    }

    @Transactional
    public void delete(Long id) {
        if (!certificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("해당 자격증/수상 정보를 찾을 수 없습니다.");
        }
        certificationRepository.deleteById(id);
    }
}
