-- ════════════════════════════════════════════════
--  V4 : 학위/교육 및 자격증/공모전 테이블 추가
-- ════════════════════════════════════════════════

-- ── education ─────────────────────────────────
CREATE TABLE education (
    id           BIGSERIAL    PRIMARY KEY,
    institution  VARCHAR(100) NOT NULL,
    degree       VARCHAR(50)  NOT NULL,
    major        VARCHAR(100) NOT NULL,
    start_date   VARCHAR(20),
    end_date     VARCHAR(20),
    sort_order   INT          DEFAULT 0
);

-- ── certification ─────────────────────────────
-- type : CERT(자격증) | AWARD(공모전/수상)
CREATE TABLE certification (
    id            BIGSERIAL    PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    issuer        VARCHAR(100),
    acquired_date VARCHAR(20),
    type          VARCHAR(10)  NOT NULL DEFAULT 'CERT',
    sort_order    INT          DEFAULT 0
);
