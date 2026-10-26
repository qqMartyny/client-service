CREATE TABLE client_account
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name   TEXT NOT NULL,
    gender      VARCHAR(1),
    status      VARCHAR(20) DEFAULT 'ACTIVE',
    create_dttm TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    modify_dttm TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    CONSTRAINT chk_client_account_gender CHECK (gender IN ('M', 'F')),
    CONSTRAINT chk_client_account_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
