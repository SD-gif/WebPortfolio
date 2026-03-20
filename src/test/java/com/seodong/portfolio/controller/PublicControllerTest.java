package com.seodong.portfolio.controller;

import com.seodong.portfolio.certification.CertificationController;
import com.seodong.portfolio.certification.CertificationService;
import com.seodong.portfolio.certification.dto.CertificationResponse;
import com.seodong.portfolio.common.dto.PageResponse;
import com.seodong.portfolio.education.EducationController;
import com.seodong.portfolio.education.EducationService;
import com.seodong.portfolio.education.dto.EducationResponse;
import com.seodong.portfolio.profile.ProfileController;
import com.seodong.portfolio.profile.ProfileService;
import com.seodong.portfolio.profile.dto.ProfileResponse;
import com.seodong.portfolio.project.ProjectController;
import com.seodong.portfolio.project.ProjectService;
import com.seodong.portfolio.project.dto.ProjectDetailResponse;
import com.seodong.portfolio.project.dto.ProjectSummaryResponse;
import com.seodong.portfolio.skill.SkillController;
import com.seodong.portfolio.skill.SkillService;
import com.seodong.portfolio.skill.dto.SkillListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.seodong.portfolio.common.security.CustomUserDetailsService;
import com.seodong.portfolio.common.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ProfileController.class, SkillController.class,
        EducationController.class, CertificationController.class, ProjectController.class})
@AutoConfigureMockMvc(addFilters = false)
class PublicControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean JwtProvider jwtProvider;
    @MockBean CustomUserDetailsService customUserDetailsService;

    @MockBean ProfileService profileService;
    @MockBean SkillService skillService;
    @MockBean EducationService educationService;
    @MockBean CertificationService certificationService;
    @MockBean ProjectService projectService;

    @Test
    @DisplayName("GET /api/profile - 프로필 조회 200 반환")
    void getProfile_returns200() throws Exception {
        ProfileResponse profile = new ProfileResponse("서동", "Backend Developer",
                "test@test.com", "https://github.com/test", "자기소개", 10, 2);
        given(profileService.getProfile()).willReturn(profile);

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("서동"))
                .andExpect(jsonPath("$.role").value("Backend Developer"));
    }

    @Test
    @DisplayName("GET /api/skills - 스킬 목록 조회 200 반환")
    void getSkills_returns200() throws Exception {
        given(skillService.getSkills()).willReturn(new SkillListResponse(List.of()));

        mockMvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray());
    }

    @Test
    @DisplayName("GET /api/educations - 학력 목록 조회 200 반환")
    void getEducations_returns200() throws Exception {
        EducationResponse edu = new EducationResponse(1L, "한국대학교", "학사", "컴퓨터공학",
                "2018-03", "2022-02", 1);
        given(educationService.getAll()).willReturn(List.of(edu));

        mockMvc.perform(get("/api/educations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].institution").value("한국대학교"));
    }

    @Test
    @DisplayName("GET /api/certifications - 자격증 목록 조회 200 반환")
    void getCertifications_returns200() throws Exception {
        CertificationResponse cert = new CertificationResponse(1L, "정보처리기사",
                "한국산업인력공단", "2023-05-01", "CERT", 1);
        given(certificationService.getAll()).willReturn(List.of(cert));

        mockMvc.perform(get("/api/certifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("정보처리기사"));
    }

    @Test
    @DisplayName("GET /api/projects - 프로젝트 목록 조회 200 반환")
    void getProjects_returns200() throws Exception {
        ProjectSummaryResponse summary = new ProjectSummaryResponse(1L, "프로젝트",
                "설명", List.of("Java"), "https://github.com", "https://demo.com",
                LocalDate.of(2024, 1, 1));
        PageResponse<ProjectSummaryResponse> page = new PageResponse<>(
                List.of(summary), 1L, 1, 0);
        given(projectService.getProjects(any())).willReturn(page);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("프로젝트"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/projects?page=1&size=5 - 페이지 파라미터 전달")
    void getProjects_withPageParams_returns200() throws Exception {
        PageResponse<ProjectSummaryResponse> page = new PageResponse<>(
                List.of(), 0L, 0, 1);
        given(projectService.getProjects(any())).willReturn(page);

        mockMvc.perform(get("/api/projects").param("page", "1").param("size", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/projects/{id} - 단건 조회 200 반환")
    void getProjectById_returns200() throws Exception {
        ProjectDetailResponse detail = new ProjectDetailResponse(1L, "프로젝트",
                "상세 설명", List.of("Java", "Spring"), "https://github.com",
                "https://demo.com", List.of("기능1"), LocalDate.of(2024, 1, 1));
        given(projectService.getProject(1L)).willReturn(detail);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("프로젝트"))
                .andExpect(jsonPath("$.features[0]").value("기능1"));
    }
}
