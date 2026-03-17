package com.seodong.portfolio.profile;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        return profileRepository.findAll().stream()
                .findFirst()
                .map(ProfileResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("프로필을 찾을 수 없습니다."));
    }
}
