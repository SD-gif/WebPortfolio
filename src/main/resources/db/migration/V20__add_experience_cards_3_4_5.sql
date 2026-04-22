-- 경험 카드 3·4·5 추가.
-- 3: Hackhub GitHub OAuth 로그인 (linked: 다커 해커톤)
-- 4: Gitalk Webhook 방 단위 라우팅 (linked: Gitalk)
-- 5: Piggymetrics Maven → Gradle 멀티 모듈 이관 (linked: Piggymetrics)

DO $$
DECLARE
    card3_id BIGINT;
    card4_id BIGINT;
    card5_id BIGINT;
    hackhub_pid BIGINT;
    gitalk_pid  BIGINT;
    piggy_pid   BIGINT;
BEGIN
    SELECT id INTO hackhub_pid FROM project WHERE title = '다커(Daker) 해커톤' LIMIT 1;
    SELECT id INTO gitalk_pid  FROM project WHERE title = 'Gitalk' LIMIT 1;
    SELECT id INTO piggy_pid   FROM project WHERE title = 'Piggymetrics (Gradle 마이그레이션)' LIMIT 1;

    -- ───────── 카드 3: GitHub OAuth 로그인 리다이렉트 흐름 ─────────
    INSERT INTO experience (title, sort_order, linked_project_id)
    VALUES ('GitHub OAuth 로그인 리다이렉트 흐름', 3, hackhub_pid)
    RETURNING id INTO card3_id;

    INSERT INTO experience_tech_stack (experience_id, tech, sort_order) VALUES
        (card3_id, 'Spring Security', 1),
        (card3_id, 'OAuth 2.0',       2),
        (card3_id, 'GitHub API',      3),
        (card3_id, 'JWT',             4);

    INSERT INTO experience_situation_item (experience_id, sort_order, content) VALUES
        (card3_id, 0, '개별 계정 관리 부담 : 참가자·심사위원 모두 자체 회원가입이 필요해 계정 관리 비용과 이탈이 발생했다.'),
        (card3_id, 1, '환경별 콜백 URL : 로컬/개발/운영이 다른 도메인을 써서 redirect URI 를 환경마다 다르게 관리해야 했다.');

    INSERT INTO experience_approach_item (experience_id, sort_order, content) VALUES
        (card3_id, 0, 'Authorization Code Flow : authorization code 를 받아 서버에서 access_token 교환 후 프로필을 조회해 가입·로그인했다.'),
        (card3_id, 1, '서버 JWT 발급 : GitHub 로그인 성공 시 자체 JWT 를 발급해 이후 API 인증을 일원화했다.'),
        (card3_id, 2, '환경별 URL 분리 : FRONTEND_URL·GITHUB_REDIRECT_URI 를 application-{profile}.yml 로 분리해 각 환경에서 일관 동작.');

    INSERT INTO experience_learned_item (experience_id, sort_order, content) VALUES
        (card3_id, 0, '외부 OAuth 분리 원칙 : 인증은 외부 공급자에 위임하고 내부 권한은 서비스 JWT 로 처리하는 2계층 구조가 유지보수에 유리했다.'),
        (card3_id, 1, '환경 설정 외부화 : redirect URI 같은 환경 의존값은 코드가 아닌 설정에 둬야 배포 파이프라인이 꼬이지 않는다.');

    -- ───────── 카드 4: Webhook 이벤트의 채팅방 단위 라우팅 ─────────
    INSERT INTO experience (title, sort_order, linked_project_id)
    VALUES ('Webhook 이벤트의 채팅방 단위 라우팅', 4, gitalk_pid)
    RETURNING id INTO card4_id;

    INSERT INTO experience_tech_stack (experience_id, tech, sort_order) VALUES
        (card4_id, 'GitHub Webhook', 1),
        (card4_id, 'HMAC',           2),
        (card4_id, 'MongoDB',        3),
        (card4_id, 'MySQL',          4);

    INSERT INTO experience_situation_item (experience_id, sort_order, content) VALUES
        (card4_id, 0, '한 레포 다수 채팅방 : 여러 팀이 같은 레포를 써도 각자의 채팅방만 해당 이벤트를 받아야 했다.'),
        (card4_id, 1, '웹훅 중복 등록 위험 : 레포 하나에 같은 URL 의 Webhook 을 여러 번 등록하면 중복 이벤트·삭제 실수가 쉽게 발생한다.');

    INSERT INTO experience_approach_item (experience_id, sort_order, content) VALUES
        (card4_id, 0, '방별 고유 secret 발급 : 채팅방 생성 시 랜덤 secret 을 만들어 Webhook 등록에 사용하고 DB 에 매핑을 저장했다.'),
        (card4_id, 1, 'HMAC 으로 방 식별 : 들어온 이벤트의 HMAC 서명을 각 secret 으로 검증해 어느 방으로 보낼지 O(1) 로 결정했다.'),
        (card4_id, 2, '미존재 시 자동 등록 : 레포의 Webhook 목록을 조회해 우리 URL 이 없으면 등록하고 있으면 재사용한다.');

    INSERT INTO experience_learned_item (experience_id, sort_order, content) VALUES
        (card4_id, 0, 'secret 을 라우팅 키로 : HMAC secret 을 라우팅 식별자로 재활용하면 추가 메타데이터 없이 경로 결정이 가능하다.'),
        (card4_id, 1, '멱등한 Webhook 관리 : 등록 API 는 "없으면 만들고 있으면 그대로"라는 멱등성을 보장해야 재시도·재배포에 안전하다.');

    -- ───────── 카드 5: Maven → Gradle 멀티 모듈 이관 ─────────
    INSERT INTO experience (title, sort_order, linked_project_id)
    VALUES ('Maven → Gradle 멀티 모듈 이관', 5, piggy_pid)
    RETURNING id INTO card5_id;

    INSERT INTO experience_tech_stack (experience_id, tech, sort_order) VALUES
        (card5_id, 'Gradle',       1),
        (card5_id, 'Spring Cloud', 2),
        (card5_id, 'MSA',          3);

    INSERT INTO experience_situation_item (experience_id, sort_order, content) VALUES
        (card5_id, 0, '원본이 Maven pom : 오리지널 Piggymetrics 는 각 서비스가 pom.xml 로 구성돼 팀 Gradle 관례와 맞지 않았다.'),
        (card5_id, 1, '서비스별 의존성 편차 : 마이크로서비스마다 의존성 버전이 제각각이라 버전 충돌·중복 명시가 빈번했다.');

    INSERT INTO experience_approach_item (experience_id, sort_order, content) VALUES
        (card5_id, 0, '루트 settings.gradle 재구성 : config·registry·gateway·auth·account·statistics·notification 7개를 하위 모듈로 묶어 일괄 빌드.'),
        (card5_id, 1, '의존성 중앙화 : 버전 상수를 루트에 모아두고 각 모듈은 필요한 것만 선언하도록 정리해 중복을 제거했다.'),
        (card5_id, 2, '레거시 의존성 교체 : Hystrix 같은 deprecated 라이브러리를 Resilience4j 로 교체하며 이관 기회에 정리했다.');

    INSERT INTO experience_learned_item (experience_id, sort_order, content) VALUES
        (card5_id, 0, '이관은 정리 기회 : 빌드 도구 교체 타이밍이 쌓여 있던 기술 부채를 정리하는 가장 자연스러운 시점이다.'),
        (card5_id, 1, '멀티 모듈 일관성 : MSA 에서도 하나의 빌드 단위로 묶어야 CI/CD 와 버전 관리가 단순해진다.');
END $$;
