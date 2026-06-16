DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'gr9app') THEN
        CREATE ROLE gr9app LOGIN;
    END IF;
END
$$;

GRANT rds_iam TO gr9app;
GRANT CONNECT ON DATABASE gr9_dev TO gr9app;

CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS stop_signs (
    id SERIAL PRIMARY KEY,
    jartic_id VARCHAR(32) UNIQUE,
    bearing INT,
    geom geography(Point, 4326)
);

CREATE INDEX IF NOT EXISTS idx_stop_sings_geom ON stop_signs USING gist(geom);

GRANT USAGE ON SCHEMA public TO gr9app;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO gr9app;
