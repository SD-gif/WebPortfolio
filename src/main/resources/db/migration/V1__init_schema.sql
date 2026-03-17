-- ════════════════════════════════════════════════
--  V1 : 초기 스키마 생성
-- ════════════════════════════════════════════════

-- ── profile ──────────────────────────────────────
CREATE TABLE profile (
    id              BIGSERIAL   PRIMARY KEY,
    name            VARCHAR(50)  NOT NULL,
    role            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL,
    github_url      VARCHAR(255),
    bio             TEXT,
    project_count   INT          DEFAULT 0,
    exp_years       INT          DEFAULT 0,
    updated_at      TIMESTAMP    DEFAULT NOW()
);

-- ── project ──────────────────────────────────────
CREATE TABLE project (
    id              BIGSERIAL    PRIMARY KEY,
    title           VARCHAR(100) NOT NULL,
    description     TEXT,
    github_url      VARCHAR(255),
    demo_url        VARCHAR(255),
    sort_order      INT          DEFAULT 0,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP    DEFAULT NOW()
);

-- ── project_tech_stack ───────────────────────────
CREATE TABLE project_tech_stack (
    id              BIGSERIAL   PRIMARY KEY,
    project_id      BIGINT      NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    tech            VARCHAR(50) NOT NULL,
    sort_order      INT         DEFAULT 0
);

-- ── project_feature ──────────────────────────────
CREATE TABLE project_feature (
    id              BIGSERIAL    PRIMARY KEY,
    project_id      BIGINT       NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    feature         VARCHAR(200) NOT NULL,
    sort_order      INT          DEFAULT 0
);

-- ── skill_category ───────────────────────────────
CREATE TABLE skill_category (
    id              BIGSERIAL   PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    sort_order      INT         DEFAULT 0
);

-- ── skill ─────────────────────────────────────────
CREATE TABLE skill (
    id              BIGSERIAL   PRIMARY KEY,
    category_id     BIGINT      NOT NULL REFERENCES skill_category(id) ON DELETE CASCADE,
    name            VARCHAR(50) NOT NULL,
    sort_order      INT         DEFAULT 0
);

-- ── contact ──────────────────────────────────────
CREATE TABLE contact (
    id              BIGSERIAL    PRIMARY KEY,
    name            VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL,
    message         TEXT         NOT NULL,
    is_read         BOOLEAN      DEFAULT FALSE,
    created_at      TIMESTAMP    DEFAULT NOW()
);

-- ── admin ─────────────────────────────────────────
CREATE TABLE admin (
    id              BIGSERIAL    PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP    DEFAULT NOW()
);

-- ── 인덱스 ───────────────────────────────────────
CREATE INDEX idx_project_sort       ON project(sort_order);
CREATE INDEX idx_contact_created    ON contact(created_at DESC);
CREATE INDEX idx_contact_is_read    ON contact(is_read);
CREATE INDEX idx_tech_stack_project ON project_tech_stack(project_id);
CREATE INDEX idx_feature_project    ON project_feature(project_id);
CREATE INDEX idx_skill_category     ON skill(category_id);
