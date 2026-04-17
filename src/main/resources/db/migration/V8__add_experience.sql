CREATE TABLE experience (
    id         BIGSERIAL    PRIMARY KEY,
    icon       VARCHAR(10),
    title      VARCHAR(100) NOT NULL,
    situation  TEXT         NOT NULL,
    approach   TEXT         NOT NULL,
    learned    TEXT         NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE experience_tech_stack (
    id            BIGSERIAL   PRIMARY KEY,
    experience_id BIGINT      NOT NULL REFERENCES experience(id) ON DELETE CASCADE,
    tech          VARCHAR(50) NOT NULL,
    sort_order    INT         NOT NULL DEFAULT 0
);

INSERT INTO experience (icon, title, situation, approach, learned, sort_order) VALUES
('🔒',
 '강의 좌석 예약의 데이터 정합성 확보',
 '강의별 최대 수용 인원을 Lecture 컬럼으로 두고, Reservation 건수가 이를 초과하지 않도록 막아야 했다. 단순 조회·검증·삽입 로직은 동시 요청이 몰리면 같은 시점에 "아직 자리 있음"을 읽고 모두 INSERT하는 경쟁 상태에 노출됐다.',
 '충돌이 예외가 아니라 예약 오픈 시점의 일상이라 판단, 낙관적 락의 재시도 대신 비관적 락을 택했다. Lecture 조회에 @Lock(PESSIMISTIC_WRITE)를 걸어 JPA가 SELECT ... FOR UPDATE를 발행하게 하고, 같은 트랜잭션 안에서 현재 예약 수를 집계·검증·삽입까지 처리했다. 락 경합 시간을 줄이기 위해 트랜잭션 범위를 예약 로직 한 메서드로 좁혔다.',
 '읽고-계산하고-쓰는 로직은 트랜잭션 경계와 락 범위가 일치해야 안전하다는 원칙을 얻었다. 트래픽이 커지면 단일 DB 락은 한계가 있어 Redisson 분산 락을 다음 선택지로 두고 있다.',
 1);

INSERT INTO experience_tech_stack (experience_id, tech, sort_order)
SELECT id, 'Spring Data JPA', 1 FROM experience WHERE title = '강의 좌석 예약의 데이터 정합성 확보'
UNION ALL
SELECT id, 'PostgreSQL', 2 FROM experience WHERE title = '강의 좌석 예약의 데이터 정합성 확보';
