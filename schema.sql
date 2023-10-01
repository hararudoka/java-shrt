DROP TABLE IF EXISTS links;

CREATE TABLE IF NOT EXISTS links (
    url VARCHAR UNIQUE,
    short VARCHAR UNIQUE
);

INSERT INTO links (url, short) VALUES ('https://example.com/mypath', 'AAAAAA');
