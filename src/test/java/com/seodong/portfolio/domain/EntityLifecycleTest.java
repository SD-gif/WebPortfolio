package com.seodong.portfolio.domain;

import com.seodong.portfolio.admin.Admin;
import com.seodong.portfolio.contact.Contact;
import com.seodong.portfolio.profile.Profile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EntityLifecycleTest {

    @Test
    @DisplayName("Admin.prePersist() 호출 시 createdAt이 설정된다")
    void admin_prePersist_setsCreatedAt() {
        Admin admin = Admin.builder().username("admin").password("encoded").build();
        admin.prePersist();
        assertThat(admin.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Contact.prePersist() 호출 시 createdAt이 설정된다")
    void contact_prePersist_setsCreatedAt() {
        Contact contact = Contact.builder().name("홍길동").email("h@test.com").message("내용").build();
        contact.prePersist();
        assertThat(contact.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Contact.markAsRead() 호출 시 isRead가 true가 된다")
    void contact_markAsRead_setsIsReadTrue() {
        Contact contact = Contact.builder().name("홍길동").email("h@test.com").message("내용").build();
        assertThat(contact.isRead()).isFalse();
        contact.markAsRead();
        assertThat(contact.isRead()).isTrue();
    }

    @Test
    @DisplayName("Profile.update() 호출 시 모든 필드가 업데이트된다")
    void profile_update_updatesAllFields() {
        Profile profile = Profile.builder()
                .name("old").role("old role").email("old@test.com")
                .githubUrl("").bio("").projectCount(0).expYears(0).build();

        profile.update("서동", "Backend Dev", "new@test.com",
                "https://github.com/new", "새 자기소개", 15, 3);

        assertThat(profile.getName()).isEqualTo("서동");
        assertThat(profile.getRole()).isEqualTo("Backend Dev");
        assertThat(profile.getEmail()).isEqualTo("new@test.com");
        assertThat(profile.getProjectCount()).isEqualTo(15);
        assertThat(profile.getExpYears()).isEqualTo(3);
    }
}
