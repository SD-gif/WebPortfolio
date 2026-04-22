-- 프로젝트 상세 페이지에 content block 3개씩 추가 (Hackhub, Gitalk, Piggymetrics).
-- block_type = 'TEXT'. 이미지는 어드민에서 추후 삽입.

DO $$
DECLARE
    hackhub_pid BIGINT;
    gitalk_pid  BIGINT;
    piggy_pid   BIGINT;
BEGIN
    SELECT id INTO hackhub_pid FROM project WHERE title = '다커(Daker) 해커톤' LIMIT 1;
    SELECT id INTO gitalk_pid  FROM project WHERE title = 'Gitalk' LIMIT 1;
    SELECT id INTO piggy_pid   FROM project WHERE title = 'Piggymetrics (Gradle 마이그레이션)' LIMIT 1;

    -- ───────── Hackhub ─────────
    INSERT INTO project_content_block (project_id, block_type, content, sort_order) VALUES
    (hackhub_pid, 'TEXT',
        E'Daker 해커톤은 한 행사에 수백 명 규모가 동시 접속하는 이벤트다. 초기에는 참가자 모집·팀 매칭·심사·시상이 전부 수작업이어서 운영 부담이 빠르게 커졌다.\n\n이 서버는 해커톤 한 사이클을 처음부터 끝까지 하나의 백엔드에서 끌고 가도록 설계됐다. 참가 등록, 팀 협업 채팅, 심사위원 평가, 실시간 랭킹까지 모두 같은 도메인 안에서 일관된 상태로 흐른다.',
        1),
    (hackhub_pid, 'TEXT',
        E'아키텍처 결정은 세 축으로 정리된다.\n\n도메인 분리 — 사용자·팀·해커톤·채팅·투표·랭킹·통계 등 11개 도메인으로 분리해 확장 가능한 경계를 만들었다.\n인증은 외부, 권한은 내부 — GitHub OAuth 로 로그인 비용을 줄이고, 이후 API 호출은 서버 JWT 로 통일했다.\n실시간과 배치의 분리 — 채팅·알림은 WebSocket, 해커톤 상태 전환(UPCOMING→OPEN→CLOSED)은 @Scheduled 로 용도에 맞게 분리했다.',
        2),
    (hackhub_pid, 'TEXT',
        E'초기 배포는 SSH 서버에서 직접 빌드하는 방식이라 실패 시 원인 추적이 힘들었다.\n\n이후 Docker Hub 분리형 파이프라인으로 재설계했다. GitHub Actions 에서 이미지를 빌드·푸시하고, 서버는 pull + compose up 만 수행한다. 고정 sleep 으로 버티던 헬스체크는 /stats 엔드포인트를 최대 12회 폴링하는 상태 기반 방식으로 교체되어, 배포 속도와 실패 감지 정확도가 동시에 개선됐다.',
        3);

    -- ───────── Gitalk ─────────
    INSERT INTO project_content_block (project_id, block_type, content, sort_order) VALUES
    (gitalk_pid, 'TEXT',
        E'개발자는 이미 터미널에서 작업 시간의 대부분을 보낸다. 채팅 때문에 브라우저를 열거나 맥락을 끊고 싶지 않았다.\n\nGitalk 는 "CLI 안에서 메시지를 주고받고, 그 방이 보는 레포의 커밋·PR 이벤트까지 함께 받는다"는 단순한 전제에서 출발한다. 팀 채팅방은 GitHub 레포와 묶이고, 오픈 채팅방은 공개 주제로 누구나 참여할 수 있다.',
        1),
    (gitalk_pid, 'TEXT',
        E'데이터 특성이 다른 두 축을 하이브리드로 나눴다.\n\nMySQL 에는 사용자·채팅방·권한·Webhook 매핑 같은 구조화 메타데이터를. MongoDB 에는 대용량·가변 스키마인 메시지를 저장하고 전문 검색도 여기서 돌린다.\n\n런타임은 ChatServer 와 CLI 애플리케이션을 분리해 구성된다. ChatServer 는 포트 6000 에서 브로드캐스트만, CLI 는 입력·렌더링·로컬 상태만 담당하도록 책임을 가른다.',
        2),
    (gitalk_pid, 'TEXT',
        E'Webhook 과 OpenAI 통합이 Gitalk 의 두 축이다.\n\n채팅방 생성 시 랜덤 secret 을 발급해 해당 레포에 Webhook 을 자동 등록하고, 들어오는 이벤트의 HMAC 서명으로 어느 방에 보낼지 O(1) 로 결정한다. 레포 하나에 여러 방이 걸려도 중복 없이 정확히 라우팅된다.\n\nOpenAI API 는 GitHub 트렌딩 레포 설명을 한국어로 요약하거나, 장시간 자리를 비운 사용자의 미독 메시지를 맥락 기반으로 정리하는 데 쓰인다.',
        3);

    -- ───────── Piggymetrics ─────────
    INSERT INTO project_content_block (project_id, block_type, content, sort_order) VALUES
    (piggy_pid, 'TEXT',
        E'Piggymetrics 는 Spring Cloud 기반 MSA 참조 아키텍처로 알려진 오픈소스다. 우리 팀은 이 프로젝트를 클론 스터디 대상으로 삼아, MSA 의 핵심 패턴(서비스 디스커버리·API Gateway·Config Server·이벤트 기반 통신·회로 차단) 을 한 사이클 실습했다.\n\n단순 복제로 끝내지 않고, 실제 팀 워크플로우(Git 브랜치 전략, PR 리뷰, CI/CD) 위에서 돌리면서 협업 문법을 맞추는 것도 목표였다.',
        1),
    (piggy_pid, 'TEXT',
        E'가장 비중이 큰 기여는 Maven → Gradle 이관이었다.\n\n원본은 각 서비스가 pom.xml 로 개별 관리되던 구조. 이를 루트 settings.gradle 에서 config·registry·gateway·auth·account·statistics·notification 7개 모듈을 묶는 멀티 프로젝트 구성으로 바꿨다.\n\n버전 상수는 루트에 모으고 각 모듈은 필요한 것만 선언하도록 정리해 중복을 제거했으며, Hystrix 같은 deprecated 라이브러리는 Resilience4j 로 이관 타이밍에 함께 교체했다.',
        2),
    (piggy_pid, 'TEXT',
        E'이관 이후 빌드·운영 파이프라인까지 다듬었다.\n\nGitHub Actions 워크플로우를 도입해 main 푸시 시점에 Gradle 빌드 → Docker 이미지 → DockerHub 푸시 → EC2 배포가 한 번에 돌도록 연결했다.\n\n관측성도 Prometheus + Grafana 로 기본 대시보드를 구성해, 서비스 간 트래픽 흐름과 에러율을 한 화면에서 추적할 수 있게 만들었다. 결과적으로 MSA 의 "여러 서비스가 따로 움직이는" 특성이 일정·배포·모니터링 관점에서도 실제로 통제 가능해졌다.',
        3);
END $$;
