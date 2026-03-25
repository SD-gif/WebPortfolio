CREATE TABLE project_media (
    id         BIGSERIAL    PRIMARY KEY,
    project_id BIGINT       NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    url        VARCHAR(500) NOT NULL,
    media_type VARCHAR(10)  NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0
);
