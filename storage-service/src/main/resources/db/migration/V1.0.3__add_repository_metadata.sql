CREATE TABLE label
(
  id               BIGINT PRIMARY KEY,
  repository_id    BIGINT REFERENCES repository(id),
  name             VARCHAR(100) NOT NULL
);

ALTER TABLE repository
	ADD COLUMN createdAt TIMESTAMP,
	ADD COLUMN updatedAt TIMESTAMP,
	ADD COLUMN status VARCHAR(50);
	
UPDATE repository SET createdAt = localtimestamp;
UPDATE repository SET updatedAt = localtimestamp;
UPDATE repository SET status = 'COMPLETED';

ALTER TABLE repository
	ALTER COLUMN createdAt SET NOT NULL,
	ALTER COLUMN status SET NOT NULL;