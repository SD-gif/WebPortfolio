package com.seodong.portfolio.certification;

import com.seodong.portfolio.certification.admin.AdminCertificationService;
import com.seodong.portfolio.certification.dto.CertificationRequest;
import com.seodong.portfolio.certification.dto.CertificationResponse;
import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CertificationServiceTest {

    @Mock CertificationRepository certificationRepository;

    @InjectMocks CertificationService certificationService;
    @InjectMocks AdminCertificationService adminCertificationService;

    private Certification sampleCert() {
        return Certification.builder()
                .name("정보처리기사").issuer("한국산업인력공단")
                .acquiredDate("2022.06").type("CERT").sortOrder(1).build();
    }

    private Certification sampleAward() {
        return Certification.builder()
                .name("공모전 수상").issuer("주관기관")
                .acquiredDate("2023.11").type("AWARD").sortOrder(2).build();
    }

    @Test
    @DisplayName("자격증/수상 목록 조회 시 전체 목록을 반환한다")
    void getAll_returnsAllCertifications() {
        // given
        given(certificationRepository.findAllByOrderBySortOrderAsc())
                .willReturn(List.of(sampleCert(), sampleAward()));

        // when
        List<CertificationResponse> result = certificationService.getAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("정보처리기사");
        assertThat(result.get(0).type()).isEqualTo("CERT");
        assertThat(result.get(1).type()).isEqualTo("AWARD");
    }

    @Test
    @DisplayName("목록이 없을 때 빈 리스트를 반환한다")
    void getAll_empty_returnsEmptyList() {
        // given
        given(certificationRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of());

        // when
        List<CertificationResponse> result = certificationService.getAll();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("자격증 생성 시 저장된 결과를 반환한다")
    void create_cert_returnsSaved() {
        // given
        Certification saved = sampleCert();
        given(certificationRepository.save(any())).willReturn(saved);
        CertificationRequest req = new CertificationRequest("정보처리기사", "한국산업인력공단",
                "2022.06", "CERT", 1);

        // when
        CertificationResponse response = adminCertificationService.create(req);

        // then
        assertThat(response.name()).isEqualTo("정보처리기사");
        assertThat(response.type()).isEqualTo("CERT");
        then(certificationRepository).should().save(any(Certification.class));
    }

    @Test
    @DisplayName("공모전 생성 시 AWARD 타입으로 저장된다")
    void create_award_returnsSavedWithAwardType() {
        // given
        Certification saved = sampleAward();
        given(certificationRepository.save(any())).willReturn(saved);
        CertificationRequest req = new CertificationRequest("공모전 수상", "주관기관",
                "2023.11", "AWARD", 2);

        // when
        CertificationResponse response = adminCertificationService.create(req);

        // then
        assertThat(response.type()).isEqualTo("AWARD");
    }

    @Test
    @DisplayName("자격증 수정 시 변경된 내용을 반환한다")
    void update_existing_returnsUpdated() {
        // given
        Certification cert = sampleCert();
        given(certificationRepository.findById(1L)).willReturn(Optional.of(cert));

        CertificationRequest req = new CertificationRequest("정보보안기사", "한국산업인력공단",
                "2024.01", "CERT", 1);

        // when
        CertificationResponse response = adminCertificationService.update(1L, req);

        // then
        assertThat(response.name()).isEqualTo("정보보안기사");
    }

    @Test
    @DisplayName("존재하지 않는 자격증 수정 시 예외가 발생한다")
    void update_notFound_throwsException() {
        // given
        given(certificationRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCertificationService.update(999L,
                new CertificationRequest("이름", "기관", null, "CERT", 1)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("자격증 삭제 시 정상 삭제된다")
    void delete_existing_deletesSuccessfully() {
        // given
        given(certificationRepository.existsById(1L)).willReturn(true);

        // when
        adminCertificationService.delete(1L);

        // then
        then(certificationRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 자격증 삭제 시 예외가 발생한다")
    void delete_notFound_throwsException() {
        // given
        given(certificationRepository.existsById(999L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adminCertificationService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        then(certificationRepository).should(never()).deleteById(any());
    }
}
