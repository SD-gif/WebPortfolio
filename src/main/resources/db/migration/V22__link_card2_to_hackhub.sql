-- 카드 2 (배포 파이프라인 재설계) 를 Hackhub 프로젝트로 연결한다.
-- V19 에서 Hackhub 프로젝트가 생성되고 V17 에서 linked_project_id 컬럼이 추가됐으므로,
-- 여기서 카드 2 의 링크를 명시적으로 걸어 prod 환경에서도 "연관 프로젝트 보기" 가 표시되도록 한다.
-- 프로젝트 타이틀로 lookup 하여 ID 하드코딩을 피한다.

UPDATE experience
   SET linked_project_id = (SELECT id FROM project WHERE title = '다커(Daker) 해커톤' LIMIT 1)
 WHERE title = '배포 파이프라인 재설계';
