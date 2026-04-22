-- Experience 의 situation / approach / learned 를 단일 TEXT 컬럼에서
-- 항목 리스트(@ElementCollection)로 분리한다.
-- content 는 TEXT 로 두어 기존 seed 데이터(200자 초과)를 그대로 보존한다.
-- 신규 입력의 150자 제한은 DTO @Size 레벨에서만 강제한다.

CREATE TABLE experience_situation_item (
    experience_id BIGINT NOT NULL REFERENCES experience(id) ON DELETE CASCADE,
    sort_order    INT    NOT NULL,
    content       TEXT   NOT NULL,
    PRIMARY KEY (experience_id, sort_order)
);

CREATE TABLE experience_approach_item (
    experience_id BIGINT NOT NULL REFERENCES experience(id) ON DELETE CASCADE,
    sort_order    INT    NOT NULL,
    content       TEXT   NOT NULL,
    PRIMARY KEY (experience_id, sort_order)
);

CREATE TABLE experience_learned_item (
    experience_id BIGINT NOT NULL REFERENCES experience(id) ON DELETE CASCADE,
    sort_order    INT    NOT NULL,
    content       TEXT   NOT NULL,
    PRIMARY KEY (experience_id, sort_order)
);

-- 기존 TEXT 값을 각 리스트의 첫 항목(sort_order=0)으로 이관
INSERT INTO experience_situation_item (experience_id, sort_order, content)
SELECT id, 0, situation FROM experience WHERE situation IS NOT NULL AND situation <> '';

INSERT INTO experience_approach_item (experience_id, sort_order, content)
SELECT id, 0, approach FROM experience WHERE approach IS NOT NULL AND approach <> '';

INSERT INTO experience_learned_item (experience_id, sort_order, content)
SELECT id, 0, learned FROM experience WHERE learned IS NOT NULL AND learned <> '';

-- 구 컬럼 제거
ALTER TABLE experience DROP COLUMN situation;
ALTER TABLE experience DROP COLUMN approach;
ALTER TABLE experience DROP COLUMN learned;
