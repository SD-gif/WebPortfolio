CREATE TABLE project_dev_point (
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT       NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    label      VARCHAR(100) NOT NULL,
    content    TEXT         NOT NULL,
    sort_order INT          NOT NULL
);

CREATE TABLE project_troubleshooting (
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT       NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    label      VARCHAR(100) NOT NULL,
    content    TEXT         NOT NULL,
    sort_order INT          NOT NULL
);
