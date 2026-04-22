-- 포트폴리오 프로젝트 3개 추가: Hackhub(Daker), Gitalk, Piggymetrics.
-- githubUrl 은 비워두고 어드민에서 나중에 채운다.

DO $$
DECLARE
    hackhub_id BIGINT;
    gitalk_id  BIGINT;
    piggy_id   BIGINT;
BEGIN
    -- ───────── Hackhub (Daker) ─────────
    INSERT INTO project (title, summary, description, sort_order, duration, team_size, role)
    VALUES (
        '다커(Daker) 해커톤',
        '팀 기반 해커톤 관리 플랫폼 백엔드',
        'Daker 해커톤 운영을 위한 풀스택 서버 플랫폼이다. 팀 매칭, 심사·투표, 실시간 채팅, 해커톤 상태 자동 전환까지 해커톤 전 과정을 지원하며, 백엔드 아키텍처 설계와 CI/CD 파이프라인 구축을 담당했다.',
        3, '2024.02 – 04', '4명', 'Backend / DevOps'
    ) RETURNING id INTO hackhub_id;

    INSERT INTO project_tech_stack (project_id, tech, sort_order) VALUES
        (hackhub_id, 'Spring Boot 3.4', 1),
        (hackhub_id, 'Java 21',         2),
        (hackhub_id, 'MySQL 8',         3),
        (hackhub_id, 'Redis',           4),
        (hackhub_id, 'JWT',             5),
        (hackhub_id, 'WebSocket',       6),
        (hackhub_id, 'Docker',          7),
        (hackhub_id, 'GitHub Actions',  8);

    INSERT INTO project_feature (project_id, feature, sort_order) VALUES
        (hackhub_id, '팀 생성 및 팀원 매칭',                     1),
        (hackhub_id, 'GitHub OAuth 로그인 + JWT 인증',           2),
        (hackhub_id, '실시간 채팅 (WebSocket/SockJS)',           3),
        (hackhub_id, '심사위원 투표 및 동적 랭킹 집계',          4),
        (hackhub_id, '파일 업로드 및 진행 상황 추적',            5),
        (hackhub_id, '라이브 통계 대시보드',                     6),
        (hackhub_id, '해커톤 상태 자동 전환 (@Scheduled)',       7);

    INSERT INTO project_dev_point (project_id, label, content, sort_order) VALUES
        (hackhub_id, '도메인 분리 설계',   '11개 도메인(팀·해커톤·사용자·인증·채팅·투표·랭킹 등)으로 계층화해 확장성을 확보했다.', 1),
        (hackhub_id, 'JWT 인증 구조',      'JwtProvider + Filter 기반 Stateless 인증에 RefreshToken 을 조합해 보안성과 확장성을 모두 잡았다.', 2),
        (hackhub_id, 'WebSocket 채팅',     'Stomp 메시지 브로커로 해커톤별 채팅방을 구현하고 SockJS 폴백을 지원했다.', 3),
        (hackhub_id, '상태 자동 전환',     '@Scheduled 로 UPCOMING→OPEN→CLOSED→ENDED 상태를 자동 전환하고 팀 마감 처리를 담당한다.', 4),
        (hackhub_id, 'QueryDSL 동적 쿼리', '랭킹·필터 조회를 타입 안정성 보장 상태로 조립하기 위해 QueryDSL 을 도입했다.', 5);

    INSERT INTO project_troubleshooting (project_id, label, content, sort_order) VALUES
        (hackhub_id, 'SockJS 보안 예외', '/ws/chat/info 핸드셰이크가 Security 에 걸려 403 → SecurityConfig PUBLIC_URLS 에 /ws/** 허용으로 해결.', 1),
        (hackhub_id, 'Docker JAR 경로',  'build/libs/*.jar 경로 규칙이 일관적이지 않아 Dockerfile 과 배포 명령을 통일해 안정화했다.', 2),
        (hackhub_id, '팀-해커톤 NPE',    '팀 참가 상태 조회 중 hackathon null 참조 → 다층 null guard 로 NPE 방지.', 3),
        (hackhub_id, '타임존 불일치',    'UTC 기본값으로 일정 계산 오류 발생 → Dockerfile/compose/application.yml 에서 Asia/Seoul 로 통일.', 4);

    -- ───────── Gitalk ─────────
    INSERT INTO project (title, summary, description, sort_order, duration, team_size, role)
    VALUES (
        'Gitalk',
        '개발자용 멀티룸 채팅 CLI — GitHub 연동 + AI 챗봇',
        'Gitalk 는 개발자 커뮤니티를 위한 CLI 기반 채팅 플랫폼이다. GitHub OAuth 로그인, 팀 채팅방의 Webhook 자동 등록으로 커밋·PR 이벤트를 실시간 수신하고, OpenAI API 로 GitHub 트렌딩 요약·뉴스를 생성한다. 메시지는 MongoDB, 사용자·채팅방 메타는 MySQL 로 하이브리드 저장하며 JLine 기반 인터랙티브 CLI 로 동작한다.',
        4, '2026.04 – 진행 중', '1명', 'Full Stack'
    ) RETURNING id INTO gitalk_id;

    INSERT INTO project_tech_stack (project_id, tech, sort_order) VALUES
        (gitalk_id, 'Java 17',      1),
        (gitalk_id, 'JLine 3',      2),
        (gitalk_id, 'MySQL 8',      3),
        (gitalk_id, 'MongoDB 5.2',  4),
        (gitalk_id, 'OpenAI API',   5),
        (gitalk_id, 'GitHub OAuth', 6),
        (gitalk_id, 'Docker',       7),
        (gitalk_id, 'Socket',       8);

    INSERT INTO project_feature (project_id, feature, sort_order) VALUES
        (gitalk_id, 'GitHub OAuth + Device Code Flow 인증',    1),
        (gitalk_id, '팀 채팅방(repo 연결) 및 오픈 채팅방',     2),
        (gitalk_id, 'GitHub Webhook 자동 등록 (HMAC 검증)',    3),
        (gitalk_id, 'MongoDB 메시지 저장 + 전문 검색',         4),
        (gitalk_id, 'OpenAI ChatGPT 트렌딩 레포 한국어 요약',  5),
        (gitalk_id, '이미지 업로드 → ASCII 아트 변환',         6),
        (gitalk_id, '소켓 기반 실시간 다중 사용자 채팅',       7),
        (gitalk_id, '미독 메시지 추적 및 공지판',              8);

    INSERT INTO project_dev_point (project_id, label, content, sort_order) VALUES
        (gitalk_id, 'Hybrid DB',              'MySQL 로 사용자·채팅방 메타, MongoDB 로 대용량 메시지를 저장해 조회·검색 특성을 모두 살렸다.', 1),
        (gitalk_id, 'Webhook 방 단위 라우팅', '방마다 고유 secret 을 발급해 Webhook 이벤트를 해당 방에만 필터링·브로드캐스트한다.', 2),
        (gitalk_id, 'Socket + CLI 분리',      'ChatServer 는 포트 6000 에서 브로드캐스트, CLI 애플리케이션은 입력·렌더링만 담당하도록 책임을 나눴다.', 3),
        (gitalk_id, 'OpenAI 통합',            '트렌딩 레포 설명 번역·뉴스 요약에 사용하며 미독 메시지 요약에도 맥락 기반으로 활용한다.', 4),
        (gitalk_id, 'Fat JAR 패키징',         'META-INF/services 누적 머지 방식으로 모든 의존성을 단일 JAR 에 담아 배포 편의성을 확보했다.', 5);

    INSERT INTO project_troubleshooting (project_id, label, content, sort_order) VALUES
        (gitalk_id, '스키마 진화',       'init.sql 의 CREATE IF NOT EXISTS 가 기존 컨테이너에서 스킵 → migration-add-webhook-columns.sql 로 컬럼 중복 방지 ALTER 를 분리했다.', 1),
        (gitalk_id, 'CRLF 처리',         '.bat 런처 배포 시 CRLF 변환이 필요해 package.sh 의 awk 파이프라인으로 자동 처리했다.', 2),
        (gitalk_id, '바이너리 권한',     'ascii-image-converter 가 macOS/Linux 에서 실행 권한 없이 배포되는 문제를 compile.sh 의 chmod +x 로 해결.', 3),
        (gitalk_id, '클래스패스 구분자', 'Windows(;) / Unix(:) 차이를 run.sh 에서 OSTYPE 으로 감지해 동적 설정하도록 고쳤다.', 4);

    -- ───────── Piggymetrics ─────────
    INSERT INTO project (title, summary, description, sort_order, duration, team_size, role)
    VALUES (
        'Piggymetrics (Gradle 마이그레이션)',
        'MSA 학습 + Maven→Gradle 마이그레이션, 팀 협업 프로젝트',
        'Piggymetrics 는 Spring Cloud 기반 MSA 참조 아키텍처 오픈소스를 팀 단위로 클론·학습한 프로젝트다. 서비스 디스커버리·API Gateway·Config Server·비동기 이벤트·회로 차단 같은 MSA 핵심 패턴을 실습했고, 원본 Maven 구성을 각 서비스별 Gradle 로 이관해 의존성·빌드 구성을 정리했다. Prometheus/Grafana 모니터링을 추가해 시스템 흐름과 에러를 관측 가능하게 만들었다.',
        5, '2024.12 – 2025.01', '6명', 'Backend · Gradle 이관'
    ) RETURNING id INTO piggy_id;

    INSERT INTO project_tech_stack (project_id, tech, sort_order) VALUES
        (piggy_id, 'Spring Cloud',         1),
        (piggy_id, 'Spring Cloud Gateway', 2),
        (piggy_id, 'Eureka',               3),
        (piggy_id, 'Config Server',        4),
        (piggy_id, 'MongoDB',              5),
        (piggy_id, 'RabbitMQ',             6),
        (piggy_id, 'Resilience4j',         7),
        (piggy_id, 'Docker',               8);

    INSERT INTO project_feature (project_id, feature, sort_order) VALUES
        (piggy_id, '다중 통화 계정 관리 및 예산 조회',              1),
        (piggy_id, '서비스 디스커버리 기반 동적 라우팅',            2),
        (piggy_id, 'API Gateway 요청 라우팅·로드 밸런싱',           3),
        (piggy_id, '분산 설정 관리 (Config Server)',                4),
        (piggy_id, 'RabbitMQ 기반 비동기 서비스 간 통신',           5),
        (piggy_id, 'Resilience4j 회로 차단기로 장애 격리',          6),
        (piggy_id, 'Prometheus 메트릭 수집·모니터링',               7);

    INSERT INTO project_dev_point (project_id, label, content, sort_order) VALUES
        (piggy_id, 'Eureka 디스커버리',   '모든 마이크로서비스가 레지스트리에 자동 등록되고 Gateway 가 서비스 ID 기반으로 자동 라우팅한다.', 1),
        (piggy_id, 'API Gateway 패턴',    'Spring Cloud Gateway 를 단일 진입점으로 두어 요청 분산·로드 밸런싱을 중앙집중식으로 처리했다.', 2),
        (piggy_id, '이벤트 기반 통신',    'RabbitMQ + Spring Cloud Bus 로 서비스 간 직접 의존성을 제거하고 설정 변경을 즉시 전파하도록 구성했다.', 3),
        (piggy_id, '회로 차단기',         'Hystrix → Resilience4j 로 마이그레이션하며 fallback 메커니즘으로 장애 시 안정성을 확보했다.', 4),
        (piggy_id, 'Maven→Gradle 이관',   '각 마이크로서비스 pom.xml 을 build.gradle 로 변환하며 의존성 관리·빌드 성능을 개선했다.', 5);

    INSERT INTO project_troubleshooting (project_id, label, content, sort_order) VALUES
        (piggy_id, 'Gradle 멀티프로젝트', '루트 settings.gradle 에서 config·registry·gateway·auth·account·statistics·notification 을 일괄 관리하도록 구성했다.', 1),
        (piggy_id, 'CI/CD 파이프라인',    'GitHub Actions 로 main 푸시 시 Gradle 빌드 → Docker 이미지 → DockerHub 푸시 → EC2 배포까지 자동화했다.', 2),
        (piggy_id, '의존성 최소화',       '서비스별 build.gradle 에서 Hystrix 등 레거시 의존성을 제거하고 Resilience4j 계열로 재정리했다.', 3);
END $$;
