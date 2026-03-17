package com.seodong.portfolio.contact.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.seodong.portfolio.contact.Contact;

import java.time.LocalDateTime;

public record ContactItemResponse(
        Long id,
        String name,
        String email,
        String message,
        @JsonProperty("isRead") boolean isRead,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt
) {
    public static ContactItemResponse from(Contact c) {
        return new ContactItemResponse(
                c.getId(), c.getName(), c.getEmail(), c.getMessage(),
                c.isRead(), c.getCreatedAt()
        );
    }
}
