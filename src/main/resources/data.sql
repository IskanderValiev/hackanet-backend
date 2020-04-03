-- UPDATE users
-- SET image_id = 16
-- WHERE users.image_id IS NULL;

CREATE TABLE IF NOT EXISTS qrtz_locks
(
    sched_name VARCHAR(120) NOT NULL,
    lock_name  VARCHAR(40)  NOT NULL,
    CONSTRAINT qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name)
);
