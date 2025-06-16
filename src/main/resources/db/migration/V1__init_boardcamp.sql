

CREATE TABLE games (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL UNIQUE,
    image          TEXT,
    stock_total    INTEGER      NOT NULL CHECK (stock_total  > 0),
    price_per_day  INTEGER      NOT NULL CHECK (price_per_day > 0)
);

CREATE TABLE customers (
    id     BIGSERIAL PRIMARY KEY,
    name   VARCHAR(100) NOT NULL,
    phone  VARCHAR(11)  NOT NULL CHECK (phone ~ '^[0-9]{10,11}$'),
    cpf    CHAR(11)     NOT NULL UNIQUE CHECK (cpf   ~ '^[0-9]{11}$')
);

CREATE TABLE rentals (
    id              BIGSERIAL PRIMARY KEY,
    customer_id     BIGINT      NOT NULL
                     REFERENCES customers(id) ON DELETE RESTRICT,
    game_id         BIGINT      NOT NULL
                     REFERENCES games(id)     ON DELETE RESTRICT,
    rent_date       DATE        NOT NULL,
    days_rented     INTEGER     NOT NULL CHECK (days_rented > 0),
    return_date     DATE,
    original_price  INTEGER     NOT NULL CHECK (original_price >= 0),
    delay_fee       INTEGER     NOT NULL DEFAULT 0 CHECK (delay_fee >= 0)
);

CREATE INDEX rentals_open_idx
  ON rentals (game_id)
  WHERE return_date IS NULL;
