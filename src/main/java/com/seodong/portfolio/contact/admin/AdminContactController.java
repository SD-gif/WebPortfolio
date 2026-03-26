package com.seodong.portfolio.contact.admin;

import com.seodong.portfolio.common.dto.PageResponse;
import com.seodong.portfolio.common.dto.SimpleResponse;
import com.seodong.portfolio.contact.dto.ContactItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - 문의")
@RestController
@RequestMapping("/api/admin/contacts")
@RequiredArgsConstructor
public class AdminContactController {

    private final AdminContactService adminContactService;

    @GetMapping
    public ResponseEntity<PageResponse<ContactItemResponse>> getContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return ResponseEntity.ok(
                adminContactService.getContacts(unreadOnly, PageRequest.of(page, size))
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<SimpleResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(adminContactService.markAsRead(id));
    }
}
