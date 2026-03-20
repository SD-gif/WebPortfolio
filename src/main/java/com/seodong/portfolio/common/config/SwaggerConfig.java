package com.seodong.portfolio.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolio API")
                        .description("서동 포트폴리오 백엔드 API 명세서\n\nAdmin API 사용 시 우측 상단 Authorize 버튼에서 JWT 토큰을 입력하세요.")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .tags(List.of(
                        new Tag().name("프로필").description("프로필 조회"),
                        new Tag().name("프로젝트").description("프로젝트 목록 / 단건 조회"),
                        new Tag().name("스킬").description("스킬 목록 조회"),
                        new Tag().name("학력").description("학력 목록 조회"),
                        new Tag().name("자격증").description("자격증 목록 조회"),
                        new Tag().name("문의").description("문의 전송"),
                        new Tag().name("Admin - 인증").description("관리자 로그인"),
                        new Tag().name("Admin - 프로필").description("프로필 수정 (JWT 필요)"),
                        new Tag().name("Admin - 프로젝트").description("프로젝트 등록 / 수정 / 삭제 (JWT 필요)"),
                        new Tag().name("Admin - 스킬").description("스킬 카테고리·항목 관리 (JWT 필요)"),
                        new Tag().name("Admin - 학력").description("학력 등록 / 수정 / 삭제 (JWT 필요)"),
                        new Tag().name("Admin - 자격증").description("자격증 등록 / 수정 / 삭제 (JWT 필요)"),
                        new Tag().name("Admin - 문의").description("문의 목록 조회 / 읽음 처리 (JWT 필요)")
                ));
    }
}
