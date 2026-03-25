package com.seodong.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seodong.portfolio.admin.AdminAuthController;
import com.seodong.portfolio.admin.AdminAuthService;
import com.seodong.portfolio.common.security.CustomUserDetailsService;
import com.seodong.portfolio.common.security.JwtProvider;
import com.seodong.portfolio.admin.dto.LoginRequest;
import com.seodong.portfolio.admin.dto.LoginResponse;
import com.seodong.portfolio.certification.admin.AdminCertificationController;
import com.seodong.portfolio.certification.admin.AdminCertificationService;
import com.seodong.portfolio.certification.dto.CertificationRequest;
import com.seodong.portfolio.certification.dto.CertificationResponse;
import com.seodong.portfolio.common.dto.PageResponse;
import com.seodong.portfolio.common.dto.SimpleResponse;
import com.seodong.portfolio.contact.admin.AdminContactController;
import com.seodong.portfolio.contact.admin.AdminContactService;
import com.seodong.portfolio.contact.dto.ContactItemResponse;
import com.seodong.portfolio.education.admin.AdminEducationController;
import com.seodong.portfolio.education.admin.AdminEducationService;
import com.seodong.portfolio.education.dto.EducationRequest;
import com.seodong.portfolio.education.dto.EducationResponse;
import com.seodong.portfolio.profile.admin.AdminProfileController;
import com.seodong.portfolio.profile.admin.AdminProfileService;
import com.seodong.portfolio.profile.dto.ProfileRequest;
import com.seodong.portfolio.profile.dto.ProfileResponse;
import com.seodong.portfolio.project.admin.AdminProjectController;
import com.seodong.portfolio.project.admin.AdminProjectService;
import com.seodong.portfolio.project.dto.ProjectDetailResponse;
import com.seodong.portfolio.project.dto.ProjectRequest;
import com.seodong.portfolio.skill.admin.AdminSkillController;
import com.seodong.portfolio.skill.admin.AdminSkillService;
import com.seodong.portfolio.skill.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({AdminAuthController.class, AdminProfileController.class,
        AdminProjectController.class, AdminSkillController.class,
        AdminContactController.class, AdminEducationController.class,
        AdminCertificationController.class})
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean JwtProvider jwtProvider;
    @MockBean CustomUserDetailsService customUserDetailsService;

    @MockBean AdminAuthService adminAuthService;
    @MockBean AdminProfileService adminProfileService;
    @MockBean AdminProjectService adminProjectService;
    @MockBean AdminSkillService adminSkillService;
    @MockBean AdminContactService adminContactService;
    @MockBean AdminEducationService adminEducationService;
    @MockBean AdminCertificationService adminCertificationService;

    // ── Auth ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/admin/login - 로그인 성공 시 200과 토큰 반환")
    void login_success_returns200() throws Exception {
        given(adminAuthService.login(any())).willReturn(new LoginResponse("jwt.token.here"));

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "pass"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"));
    }

    // ── Profile ──────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/admin/profile - 프로필 수정 200 반환")
    void updateProfile_returns200() throws Exception {
        ProfileResponse resp = new ProfileResponse("서동", "Backend Dev",
                "test@test.com", "https://github.com", "자기소개", 10, 2);
        given(adminProfileService.update(any())).willReturn(resp);

        ProfileRequest req = new ProfileRequest("서동", "Backend Dev", "test@test.com",
                "https://github.com", "자기소개", 10, 2);

        mockMvc.perform(put("/api/admin/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("서동"));
    }

    // ── Projects ─────────────────────────────────────────────

    private ProjectDetailResponse sampleDetail() {
        return new ProjectDetailResponse(1L, "프로젝트", "간략 소개", "설명",
                List.of("Java"), "https://github.com", "https://demo.com",
                List.of("기능1"), List.of(), LocalDate.of(2024, 1, 1));
    }

    private ProjectRequest sampleProjectReq() {
        return new ProjectRequest("프로젝트", "간략 소개", "설명", "https://github.com",
                "https://demo.com", 1, List.of("Java"), List.of("기능1"));
    }

    @Test
    @DisplayName("POST /api/admin/projects - 프로젝트 생성 201 반환")
    void createProject_returns201() throws Exception {
        given(adminProjectService.create(any())).willReturn(sampleDetail());

        mockMvc.perform(post("/api/admin/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProjectReq())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("프로젝트"));
    }

    @Test
    @DisplayName("PUT /api/admin/projects/{id} - 프로젝트 수정 200 반환")
    void updateProject_returns200() throws Exception {
        given(adminProjectService.update(eq(1L), any())).willReturn(sampleDetail());

        mockMvc.perform(put("/api/admin/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProjectReq())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/admin/projects/{id} - 프로젝트 삭제 204 반환")
    void deleteProject_returns204() throws Exception {
        willDoNothing().given(adminProjectService).delete(1L);

        mockMvc.perform(delete("/api/admin/projects/1"))
                .andExpect(status().isNoContent());
    }

    // ── Skills ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/admin/skills - 카테고리 목록 200 반환")
    void getSkillCategories_returns200() throws Exception {
        SkillCategoryResponse cat = new SkillCategoryResponse(1L, "Backend", 1, List.of());
        given(adminSkillService.getCategories()).willReturn(List.of(cat));

        mockMvc.perform(get("/api/admin/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Backend"));
    }

    @Test
    @DisplayName("POST /api/admin/skills/categories - 카테고리 생성 201 반환")
    void createCategory_returns201() throws Exception {
        SkillCategoryResponse resp = new SkillCategoryResponse(1L, "DevOps", 2, List.of());
        given(adminSkillService.createCategory(any())).willReturn(resp);

        mockMvc.perform(post("/api/admin/skills/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SkillCategoryRequest("DevOps", 2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("DevOps"));
    }

    @Test
    @DisplayName("PUT /api/admin/skills/categories/{id} - 카테고리 수정 200 반환")
    void updateCategory_returns200() throws Exception {
        SkillCategoryResponse resp = new SkillCategoryResponse(1L, "Updated", 1, List.of());
        given(adminSkillService.updateCategory(eq(1L), any())).willReturn(resp);

        mockMvc.perform(put("/api/admin/skills/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SkillCategoryRequest("Updated", 1))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/admin/skills/categories/{id} - 카테고리 삭제 204 반환")
    void deleteCategory_returns204() throws Exception {
        willDoNothing().given(adminSkillService).deleteCategory(1L);

        mockMvc.perform(delete("/api/admin/skills/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/admin/skills/categories/{id}/skills - 스킬 추가 201 반환")
    void addSkill_returns201() throws Exception {
        SkillItemResponse resp = new SkillItemResponse(1L, "Java", 1);
        given(adminSkillService.addSkill(eq(1L), any())).willReturn(resp);

        mockMvc.perform(post("/api/admin/skills/categories/1/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SkillRequest("Java", 1))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    @DisplayName("DELETE /api/admin/skills/{id} - 스킬 삭제 204 반환")
    void deleteSkill_returns204() throws Exception {
        willDoNothing().given(adminSkillService).deleteSkill(1L);

        mockMvc.perform(delete("/api/admin/skills/1"))
                .andExpect(status().isNoContent());
    }

    // ── Contacts ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/admin/contacts - 문의 목록 200 반환")
    void getContacts_returns200() throws Exception {
        PageResponse<ContactItemResponse> page = new PageResponse<>(List.of(), 0L, 0, 0);
        given(adminContactService.getContacts(anyBoolean(), any())).willReturn(page);

        mockMvc.perform(get("/api/admin/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/admin/contacts?unreadOnly=true - 미읽음 필터 200 반환")
    void getContacts_unreadOnly_returns200() throws Exception {
        PageResponse<ContactItemResponse> page = new PageResponse<>(List.of(), 0L, 0, 0);
        given(adminContactService.getContacts(eq(true), any())).willReturn(page);

        mockMvc.perform(get("/api/admin/contacts").param("unreadOnly", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/admin/contacts/{id}/read - 읽음 처리 200 반환")
    void markAsRead_returns200() throws Exception {
        given(adminContactService.markAsRead(1L)).willReturn(SimpleResponse.ok());

        mockMvc.perform(patch("/api/admin/contacts/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── Education ─────────────────────────────────────────────

    private EducationResponse sampleEdu() {
        return new EducationResponse(1L, "한국대", "학사", "컴퓨터공학", "2018-03", "2022-02", 1);
    }

    private EducationRequest sampleEduReq() {
        return new EducationRequest("한국대", "학사", "컴퓨터공학", "2018-03", "2022-02", 1);
    }

    @Test
    @DisplayName("POST /api/admin/educations - 학력 생성 201 반환")
    void createEducation_returns201() throws Exception {
        given(adminEducationService.create(any())).willReturn(sampleEdu());

        mockMvc.perform(post("/api/admin/educations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEduReq())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.institution").value("한국대"));
    }

    @Test
    @DisplayName("PUT /api/admin/educations/{id} - 학력 수정 200 반환")
    void updateEducation_returns200() throws Exception {
        given(adminEducationService.update(eq(1L), any())).willReturn(sampleEdu());

        mockMvc.perform(put("/api/admin/educations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEduReq())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/admin/educations/{id} - 학력 삭제 204 반환")
    void deleteEducation_returns204() throws Exception {
        willDoNothing().given(adminEducationService).delete(1L);

        mockMvc.perform(delete("/api/admin/educations/1"))
                .andExpect(status().isNoContent());
    }

    // ── Certification ─────────────────────────────────────────

    private CertificationResponse sampleCert() {
        return new CertificationResponse(1L, "정보처리기사", "한국산업인력공단", "2023-05-01", "CERT", 1);
    }

    private CertificationRequest sampleCertReq() {
        return new CertificationRequest("정보처리기사", "한국산업인력공단", "2023-05-01", "CERT", 1);
    }

    @Test
    @DisplayName("POST /api/admin/certifications - 자격증 생성 201 반환")
    void createCertification_returns201() throws Exception {
        given(adminCertificationService.create(any())).willReturn(sampleCert());

        mockMvc.perform(post("/api/admin/certifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCertReq())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("정보처리기사"));
    }

    @Test
    @DisplayName("PUT /api/admin/certifications/{id} - 자격증 수정 200 반환")
    void updateCertification_returns200() throws Exception {
        given(adminCertificationService.update(eq(1L), any())).willReturn(sampleCert());

        mockMvc.perform(put("/api/admin/certifications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCertReq())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/admin/certifications/{id} - 자격증 삭제 204 반환")
    void deleteCertification_returns204() throws Exception {
        willDoNothing().given(adminCertificationService).delete(1L);

        mockMvc.perform(delete("/api/admin/certifications/1"))
                .andExpect(status().isNoContent());
    }
}
