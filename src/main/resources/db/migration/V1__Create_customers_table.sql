CREATE TABLE IF NOT EXISTS customers (
  id      BIGSERIAL NOT NULL,
  name    VARCHAR NOT NULL,
  email   VARCHAR NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (email)
);
