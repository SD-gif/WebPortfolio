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
class AdminProfileServiceTest {

    @Mock ProfileRepository profileRepository;
    @InjectMocks AdminProfileService adminProfileService;

    private ProfileRequest sampleRequest() {
        return new ProfileRequest("서동", "Backend Developer", "test@test.com",
                "https://github.com/test", "자기소개", 10, 2);
    }

    @Test
    @DisplayName("프로필이 존재할 때 수정 후 응답을 반환한다")
    void update_existing_returnsUpdated() {
        // given
        Profile profile = Profile.builder()
                .name("old").role("old role").email("old@test.com")
                .githubUrl("").bio("").projectCount(0).expYears(0).build();

        given(profileRepository.findAll()).willReturn(List.of(profile));
        given(profileRepository.save(any())).willReturn(profile);

        // when
        ProfileResponse response = adminProfileService.update(sampleRequest());

        // then
        assertThat(response.name()).isEqualTo("서동");
        assertThat(response.role()).isEqualTo("Backend Developer");
        then(profileRepository).should().save(profile);
    }

    @Test
    @DisplayName("프로필이 없을 때 예외가 발생한다")
    void update_noProfile_throwsException() {
        // given
        given(profileRepository.findAll()).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> adminProfileService.update(sampleRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("프로필");
    }
}
