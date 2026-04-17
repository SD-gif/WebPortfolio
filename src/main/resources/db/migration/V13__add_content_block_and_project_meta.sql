-- 프로젝트 메타 정보
ALTER TABLE project ADD COLUMN duration VARCHAR(50);
ALTER TABLE project ADD COLUMN team_size VARCHAR(30);
ALTER TABLE project ADD COLUMN role VARCHAR(50);

-- 콘텐츠 블록
CREATE TABLE project_content_block (
    id         BIGSERIAL    PRIMARY KEY,
    project_id BIGINT       NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    block_type VARCHAR(10)  NOT NULL,
    content    TEXT         NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0
);
