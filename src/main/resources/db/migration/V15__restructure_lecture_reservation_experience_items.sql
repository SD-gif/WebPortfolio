-- V8 seed 로 들어간 '강의 좌석 예약의 데이터 정합성 확보' 경험의
-- situation/approach/learned 항목을 "제목 : 설명" 형태의 구조화된 bullet 로 재구성한다.
-- V14 가 단일 TEXT 를 sort_order=0 한 건으로 이관해둔 상태를 전제로,
-- 해당 row 의 기존 항목을 지우고 다시 INSERT 한다.

DO $$
DECLARE
    target_id BIGINT;
BEGIN
    SELECT id INTO target_id FROM experience
     WHERE title = '강의 좌석 예약의 데이터 정합성 확보' LIMIT 1;
    IF target_id IS NULL THEN
        RETURN;
    END IF;

    DELETE FROM experience_situation_item WHERE experience_id = target_id;
    DELETE FROM experience_approach_item  WHERE experience_id = target_id;
    DELETE FROM experience_learned_item   WHERE experience_id = target_id;

    INSERT INTO experience_situation_item (experience_id, sort_order, content) VALUES
        (target_id, 0, '수용 인원 제약 : 강의별 최대 수용 인원을 Lecture 컬럼으로 두고, Reservation 건수가 이를 초과하지 않도록 막아야 했다.'),
        (target_id, 1, '동시성 경쟁 상태 : 단순 조회·검증·삽입 로직은 동시 요청이 몰리면 같은 시점에 ''아직 자리 있음''을 읽고 모두 INSERT 하는 경쟁 상태에 노출됐다.');

    INSERT INTO experience_approach_item (experience_id, sort_order, content) VALUES
        (target_id, 0, '비관적 락 선택 : 충돌이 예외가 아니라 예약 오픈 시점의 일상이라 판단, 낙관적 락의 재시도 대신 비관적 락을 택했다.'),
        (target_id, 1, 'FOR UPDATE 적용 : Lecture 조회에 @Lock(PESSIMISTIC_WRITE) 를 걸어 JPA 가 SELECT ... FOR UPDATE 를 발행하게 하고, 같은 트랜잭션에서 예약 수 집계·검증·삽입까지 처리했다.'),
        (target_id, 2, '트랜잭션 범위 축소 : 락 경합 시간을 줄이기 위해 트랜잭션 범위를 예약 로직 한 메서드로 좁혔다.');

    INSERT INTO experience_learned_item (experience_id, sort_order, content) VALUES
        (target_id, 0, '트랜잭션-락 범위 일치 : 읽고-계산하고-쓰는 로직은 트랜잭션 경계와 락 범위가 일치해야 안전하다는 원칙을 얻었다.'),
        (target_id, 1, '다음 선택지 Redisson : 트래픽이 커지면 단일 DB 락은 한계가 있어 Redisson 분산 락을 다음 선택지로 두고 있다.');
END $$;
