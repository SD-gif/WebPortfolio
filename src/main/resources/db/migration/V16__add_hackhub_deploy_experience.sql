-- hackhub 배포 파이프라인 재설계 경험을 경험 카드 2번으로 추가한다.
-- A(Docker Hub 전환) + B(상태 기반 헬스체크) 을 한 카드로 통합.

INSERT INTO experience (icon, title, sort_order)
VALUES (NULL, '배포 파이프라인 재설계 (Docker Hub 방식 + 상태 기반 헬스체크)', 2);

DO $$
DECLARE
    target_id BIGINT;
BEGIN
    SELECT id INTO target_id FROM experience
     WHERE title = '배포 파이프라인 재설계 (Docker Hub 방식 + 상태 기반 헬스체크)' LIMIT 1;

    INSERT INTO experience_tech_stack (experience_id, tech, sort_order) VALUES
        (target_id, 'GitHub Actions', 1),
        (target_id, 'Docker',         2),
        (target_id, 'Docker Compose', 3);

    INSERT INTO experience_situation_item (experience_id, sort_order, content) VALUES
        (target_id, 0, 'SSH 서버 빌드 의존 : SSH 로 서버에서 직접 빌드하는 방식이어서 배포 시간이 길고 실패 원인 디버깅이 어려웠다.');

    INSERT INTO experience_approach_item (experience_id, sort_order, content) VALUES
        (target_id, 0, 'Docker Hub 분리 : GitHub Actions 에서 빌드·푸시, 서버는 pull + compose up 만 수행하도록 책임을 분리했다.'),
        (target_id, 1, '중복 배포 방지 : concurrency cancel-in-progress 로 동시 배포 충돌을 막고, compose 파일 변경 시에만 scp 동기화했다.');

    INSERT INTO experience_learned_item (experience_id, sort_order, content) VALUES
        (target_id, 0, '상태 기반 헬스체크 : 고정 sleep 대신 /stats 엔드포인트 12회 재시도 폴링으로 교체 — 속도·신뢰성이 모두 개선됐다.'),
        (target_id, 1, '책임 분리 원칙 : 빌드와 배포 책임을 분리하면 실패 원인을 빠르게 좁혀서 장애 대응 시간이 줄어든다.');
END $$;
