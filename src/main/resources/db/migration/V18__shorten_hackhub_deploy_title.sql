-- Experience 제목 30자 제한 도입에 맞춰 카드 2(hackhub 배포) 제목을 축약한다.
-- 기존: '배포 파이프라인 재설계 (Docker Hub 방식 + 상태 기반 헬스체크)' (41자)

UPDATE experience
   SET title = '배포 파이프라인 재설계'
 WHERE title = '배포 파이프라인 재설계 (Docker Hub 방식 + 상태 기반 헬스체크)';
