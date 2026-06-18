CREATE EXTENSION IF NOT EXISTS postgis;

-- Keep schema defaults aligned with the first Atlas migration while dev uses
-- a fixed database name/user pair (`gr9_dev` / `gr9app`).

CREATE TABLE IF NOT EXISTS stop_signs (
    id SERIAL PRIMARY KEY,
    jartic_id VARCHAR(32) UNIQUE,
    bearing INT,
    geom geography(Point, 4326)
);

CREATE INDEX IF NOT EXISTS idx_stop_signs_geom ON stop_signs USING gist(geom);
