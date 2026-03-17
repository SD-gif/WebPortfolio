package com.seodong.portfolio.profile.admin;

import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.profile.ProfileRepository;
import com.seodong.portfolio.profile.dto.ProfileRequest;
import com.seodong.portfolio.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProfileService {

    private final ProfileRepository profileRepository;

    @Transactional
    public ProfileResponse update(ProfileRequest req) {
        return profileRepository.findAll().stream()
                .findFirst()
                .map(profile -> {
                    profile.update(req.name(), req.role(), req.email(),
                            req.githubUrl(), req.bio(), req.projectCount(), req.expYears());
                    return ProfileResponse.from(profileRepository.save(profile));
                })
                .orElseThrow(() -> new ResourceNotFoundException("프로필을 찾을 수 없습니다."));
    }
}
