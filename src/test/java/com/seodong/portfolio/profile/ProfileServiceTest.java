package com.seodong.portfolio.profile;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.profile.admin.AdminProfileService;
import com.seodong.portfolio.profile.dto.ProfileRequest;
import com.seodong.portfolio.profile.dto.ProfileResponse;
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
class ProfileServiceTest {

    @Mock ProfileRepository profileRepository;

    @InjectMocks ProfileService profileService;
    @InjectMocks AdminProfileService adminProfileService;

    private Profile sampleProfile() {
        return Profile.builder()
                .name("서동").role("Backend Developer")
                .email("test@test.com").githubUrl("https://github.com/test")
                .bio("안녕하세요").projectCount(10).expYears(3)
                .build();
    }

    @Test
    @DisplayName("프로필 조회 시 정상적으로 반환한다")
    void getProfile_exists_returnsProfile() {
        // given
        given(profileRepository.findAll()).willReturn(List.of(sampleProfile()));

        // when
        ProfileResponse response = profileService.getProfile();

        // then
        assertThat(response.name()).isEqualTo("서동");
        assertThat(response.expYears()).isEqualTo(3);
    }

    @Test
    @DisplayName("프로필이 없을 때 조회하면 예외가 발생한다")
    void getProfile_notExists_throwsException() {
        // given
        given(profileRepository.findAll()).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> profileService.getProfile())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("프로필 수정 시 변경된 내용을 반환한다")
    void update_existingProfile_returnsUpdated() {
        // given
        Profile profile = sampleProfile();
        given(profileRepository.findAll()).willReturn(List.of(profile));
        given(profileRepository.save(any())).willReturn(profile);

        ProfileRequest req = new ProfileRequest("서동업데이트", "DevOps", "new@test.com",
                "https://github.com/new", "업데이트 소개", 15, 4);

        // when
        ProfileResponse response = adminProfileService.update(req);

        // then
        assertThat(response.name()).isEqualTo("서동업데이트");
        assertThat(response.role()).isEqualTo("DevOps");
    }

    @Test
    @DisplayName("프로필이 없을 때 수정하면 예외가 발생한다")
    void update_notExists_throwsException() {
        // given
        given(profileRepository.findAll()).willReturn(List.of());
        ProfileRequest req = new ProfileRequest("이름", "역할", "e@e.com", null, null, 0, 0);

        // when & then
        assertThatThrownBy(() -> adminProfileService.update(req))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
