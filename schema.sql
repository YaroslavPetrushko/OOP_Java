-- Book Manager — single-table inheritance schema
-- All book types stored in one table; nullable columns belong to specific subtypes.

CREATE TABLE IF NOT EXISTS books (
    id                  SERIAL          PRIMARY KEY,
    type                VARCHAR(16)     NOT NULL,           -- BOOK | EBOOK | AUDIOBOOK | PAPERBOOK | RAREBOOK
    title               VARCHAR(512)    NOT NULL,
    author              VARCHAR(255)    NOT NULL,
    year                INTEGER         NOT NULL CHECK (year >= 1),
    price               NUMERIC(10, 2)  NOT NULL CHECK (price >= 0),
    genre               VARCHAR(32)     NOT NULL,
    pages               INTEGER         NOT NULL CHECK (pages > 0),
    quantity            INTEGER         NOT NULL CHECK (quantity >= 1),

    -- EBook fields
    file_format         VARCHAR(16),
    file_size_mb        NUMERIC(10, 3),
    download_url        VARCHAR(1024),

    -- AudioBook fields
    narrator            VARCHAR(255),
    duration_minutes    INTEGER,
    audio_format        VARCHAR(16),

    -- PaperBook + RareBook fields
    publisher           VARCHAR(255),
    edition             INTEGER,
    weight_grams        NUMERIC(8, 2),

    -- RareBook-only fields
    condition           VARCHAR(16),
    estimated_value_usd NUMERIC(12, 2),
    acquisition_year    INTEGER
    );