-- ════════════════════════════════════════════════
--  V2 : 초기 데이터 삽입
-- ════════════════════════════════════════════════

-- ── 프로필 기본 데이터 ────────────────────────────
INSERT INTO profile (name, role, email, github_url, bio, project_count, exp_years)
VALUES (
    '서동',
    'Backend / DevOps Developer',
    'alem6516@gmail.com',
    'https://github.com/YOUR_GITHUB_ID',
    '안녕하세요. 백엔드와 DevOps를 다루는 개발자 서동입니다.',
    20,
    3
);

-- ── 스킬 카테고리 + 스킬 ──────────────────────────
INSERT INTO skill_category (name, sort_order) VALUES ('Backend', 1);
INSERT INTO skill_category (name, sort_order) VALUES ('DevOps', 2);
INSERT INTO skill_category (name, sort_order) VALUES ('Database', 3);

-- Backend 스킬
INSERT INTO skill (category_id, name, sort_order)
SELECT id, unnest(ARRAY['Java', 'Spring Boot', 'QueryDSL', 'JPA']),
       generate_series(1, 4)
FROM skill_category WHERE name = 'Backend';

-- DevOps 스킬
INSERT INTO skill (category_id, name, sort_order)
SELECT id, unnest(ARRAY['Docker', 'Kubernetes', 'AWS', 'CI/CD', 'Jenkins']),
       generate_series(1, 5)
FROM skill_category WHERE name = 'DevOps';

-- Database 스킬
INSERT INTO skill (category_id, name, sort_order)
SELECT id, unnest(ARRAY['PostgreSQL', 'Redis', 'MySQL']),
       generate_series(1, 3)
FROM skill_category WHERE name = 'Database';

-- ── 관리자 계정 (비밀번호: admin1234 — 반드시 교체) ──
-- BCrypt hash of 'admin1234'
INSERT INTO admin (username, password)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyPQDHJ4u');
