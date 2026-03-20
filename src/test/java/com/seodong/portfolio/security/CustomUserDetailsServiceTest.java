package com.seodong.portfolio.security;

import com.seodong.portfolio.admin.Admin;
import com.seodong.portfolio.admin.AdminRepository;
import com.seodong.portfolio.common.security.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock AdminRepository adminRepository;
    @InjectMocks CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("존재하는 username으로 조회 시 UserDetails를 반환한다")
    void loadUserByUsername_existing_returnsUserDetails() {
        // given
        Admin admin = Admin.builder().username("admin").password("encoded").build();
        given(adminRepository.findByUsername("admin")).willReturn(Optional.of(admin));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        // then
        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("존재하지 않는 username으로 조회 시 예외가 발생한다")
    void loadUserByUsername_notFound_throwsException() {
        // given
        given(adminRepository.findByUsername("unknown")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
