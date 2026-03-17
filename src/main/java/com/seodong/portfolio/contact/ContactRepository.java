package com.seodong.portfolio.contact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Page<Contact> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Contact> findByIsReadFalseOrderByCreatedAtDesc(Pageable pageable);
}
