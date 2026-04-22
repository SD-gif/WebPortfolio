-- Experience 카드에서 상세 페이지로 이동할 수 있도록 연관 프로젝트 참조를 추가한다.
-- 프로젝트 삭제 시 카드는 유지하고 링크만 끊도록 ON DELETE SET NULL.

ALTER TABLE experience
    ADD COLUMN linked_project_id BIGINT NULL REFERENCES project(id) ON DELETE SET NULL;
