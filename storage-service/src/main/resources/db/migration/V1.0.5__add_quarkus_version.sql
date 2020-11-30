CREATE TABLE quarkus_version
(
  id               BIGINT PRIMARY KEY,
  version          VARCHAR(50) NOT NULL UNIQUE
);

ALTER TABLE repository ADD COLUMN quarkus_version_id BIGINT REFERENCES quarkus_version(id);
