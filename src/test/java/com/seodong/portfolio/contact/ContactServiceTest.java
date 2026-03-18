package com.seodong.portfolio.contact;

import com.seodong.portfolio.common.dto.MessageResponse;
import com.seodong.portfolio.common.dto.PageResponse;
import com.seodong.portfolio.common.dto.SimpleResponse;
import com.seodong.portfolio.common.exception.RateLimitException;
import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.contact.admin.AdminContactService;
import com.seodong.portfolio.contact.dto.ContactItemResponse;
import com.seodong.portfolio.contact.dto.ContactRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock ContactRepository contactRepository;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock JavaMailSender mailSender;
    @Mock ValueOperations<String, String> valueOperations;

    @InjectMocks ContactService contactService;
    @InjectMocks AdminContactService adminContactService;

    private void setupContactService() {
        ReflectionTestUtils.setField(contactService, "fromEmail", "test@test.com");
        ReflectionTestUtils.setField(contactService, "rateLimitCapacity", 3);
        ReflectionTestUtils.setField(contactService, "rateLimitRefillMinutes", 1L);
    }

    @Test
    @DisplayName("문의 전송 시 DB 저장 및 이메일 발송이 이루어진다")
    void sendContact_valid_savesAndSendsMail() {
        // given
        setupContactService();
        ContactRequest req = new ContactRequest("홍길동", "hong@test.com", "문의 내용입니다.");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(anyString())).willReturn(1L);
        given(contactRepository.save(any())).willReturn(Contact.builder()
                .name("홍길동").email("hong@test.com").message("문의 내용입니다.").build());

        // when
        MessageResponse response = contactService.sendContact(req, "127.0.0.1");

        // then
        assertThat(response.success()).isTrue();
        then(contactRepository).should().save(any(Contact.class));
        then(mailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("레이트 리밋 초과 시 예외가 발생한다")
    void sendContact_rateLimitExceeded_throwsException() {
        // given
        setupContactService();
        ContactRequest req = new ContactRequest("홍길동", "hong@test.com", "문의");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(anyString())).willReturn(4L); // capacity=3 초과

        // when & then
        assertThatThrownBy(() -> contactService.sendContact(req, "127.0.0.1"))
                .isInstanceOf(RateLimitException.class);
        then(contactRepository).should(never()).save(any());
        then(mailSender).should(never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("전체 문의 목록 조회 시 페이지 결과를 반환한다")
    void getContacts_all_returnsPage() {
        // given
        Contact contact = Contact.builder().name("홍길동").email("h@h.com").message("내용").build();
        PageRequest pageable = PageRequest.of(0, 15);
        given(contactRepository.findAllByOrderByCreatedAtDesc(pageable))
                .willReturn(new PageImpl<>(List.of(contact)));

        // when
        PageResponse<ContactItemResponse> result = adminContactService.getContacts(false, pageable);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("미읽음 필터 적용 시 미읽음 문의만 반환한다")
    void getContacts_unreadOnly_returnsUnread() {
        // given
        Contact unread = Contact.builder().name("미읽음").email("u@u.com").message("내용").build();
        PageRequest pageable = PageRequest.of(0, 15);
        given(contactRepository.findByIsReadFalseOrderByCreatedAtDesc(pageable))
                .willReturn(new PageImpl<>(List.of(unread)));

        // when
        PageResponse<ContactItemResponse> result = adminContactService.getContacts(true, pageable);

        // then
        assertThat(result.content()).hasSize(1);
    }

    @Test
    @DisplayName("문의 읽음 처리 시 정상적으로 처리된다")
    void markAsRead_existing_marksSuccessfully() {
        // given
        Contact contact = Contact.builder().name("홍길동").email("h@h.com").message("내용").build();
        given(contactRepository.findById(1L)).willReturn(Optional.of(contact));

        // when
        SimpleResponse response = adminContactService.markAsRead(1L);

        // then
        assertThat(response.success()).isTrue();
        assertThat(contact.isRead()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 문의 읽음 처리 시 예외가 발생한다")
    void markAsRead_notFound_throwsException() {
        // given
        given(contactRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminContactService.markAsRead(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
