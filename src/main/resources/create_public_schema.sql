CREATE TABLE IF NOT EXISTS public.users (
    id UUID PRIMARY KEY,
    schema_name VARCHAR(255) NOT NULL UNIQUE,
    account_number BIGINT NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_registration_date CHECK (registration_date <= CURRENT_TIMESTAMP)
);