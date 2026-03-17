package com.seodong.portfolio.contact.admin;

import com.seodong.portfolio.common.dto.PageResponse;
import com.seodong.portfolio.common.dto.SimpleResponse;
import com.seodong.portfolio.common.exception.ResourceNotFoundException;
import com.seodong.portfolio.contact.ContactRepository;
import com.seodong.portfolio.contact.dto.ContactItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminContactService {

    private final ContactRepository contactRepository;

    @Transactional(readOnly = true)
    public PageResponse<ContactItemResponse> getContacts(boolean unreadOnly, Pageable pageable) {
        return PageResponse.from(
                unreadOnly
                        ? contactRepository.findByIsReadFalseOrderByCreatedAtDesc(pageable)
                                .map(ContactItemResponse::from)
                        : contactRepository.findAllByOrderByCreatedAtDesc(pageable)
                                .map(ContactItemResponse::from)
        );
    }

    @Transactional
    public SimpleResponse markAsRead(Long id) {
        contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 문의를 찾을 수 없습니다."))
                .markAsRead();
        return SimpleResponse.ok();
    }
}
