CREATE TABLE quarkus_version
(
  id          VARCHAR(50) PRIMARY KEY
);

ALTER TABLE repository ADD COLUMN quarkus_version_id VARCHAR(50) REFERENCES quarkus_version(id);
ALTER TABLE quarkus_extension ADD COLUMN quarkus_version_id VARCHAR(50) REFERENCES quarkus_version(id);
