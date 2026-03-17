package com.seodong.portfolio.profile;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // 프로필은 단일 레코드 — findAll().get(0) 또는 findById(1L) 로 사용
}
