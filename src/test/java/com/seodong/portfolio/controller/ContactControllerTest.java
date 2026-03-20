package com.seodong.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seodong.portfolio.common.dto.MessageResponse;
import com.seodong.portfolio.common.security.CustomUserDetailsService;
import com.seodong.portfolio.common.security.JwtProvider;
import com.seodong.portfolio.contact.ContactController;
import com.seodong.portfolio.contact.ContactService;
import com.seodong.portfolio.contact.dto.ContactRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomUserDetailsService customUserDetailsService;
    @MockBean ContactService contactService;

    @Test
    @DisplayName("POST /api/contact - 유효한 요청 시 201 반환")
    void sendContact_valid_returns201() throws Exception {
        ContactRequest req = new ContactRequest("홍길동", "hong@test.com", "문의 내용입니다.");
        MessageResponse resp = new MessageResponse(true, "문의가 정상적으로 전송되었습니다.");
        given(contactService.sendContact(any(ContactRequest.class), anyString())).willReturn(resp);

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("문의가 정상적으로 전송되었습니다."));
    }

    @Test
    @DisplayName("POST /api/contact - 이름 누락 시 400 반환")
    void sendContact_missingName_returns400() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"test@test.com\",\"message\":\"내용\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/contact - 이메일 형식 오류 시 400 반환")
    void sendContact_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"홍길동\",\"email\":\"not-email\",\"message\":\"내용\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/contact - X-Forwarded-For 헤더 IP 전달")
    void sendContact_withForwardedIp_usesForwardedIp() throws Exception {
        ContactRequest req = new ContactRequest("홍길동", "hong@test.com", "문의 내용입니다.");
        given(contactService.sendContact(any(), eq("10.0.0.1")))
                .willReturn(new MessageResponse(true, "문의가 정상적으로 전송되었습니다."));

        mockMvc.perform(post("/api/contact")
                        .header("X-Forwarded-For", "10.0.0.1, 192.168.0.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        then(contactService).should().sendContact(any(), eq("10.0.0.1"));
    }
}
