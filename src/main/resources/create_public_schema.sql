CREATE TABLE IF NOT EXISTS public.users (
    "account_number" BIGINT PRIMARY KEY,
    "schema" UUID NOT NULL UNIQUE,
    "password" VARCHAR(255) NOT NULL,
    "registration_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "last_login" TIMESTAMP,
    CONSTRAINT chk_registration_date CHECK (registration_date <= CURRENT_TIMESTAMP)
);