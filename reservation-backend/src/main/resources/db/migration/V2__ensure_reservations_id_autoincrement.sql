-- Repairs tables created without AUTO_INCREMENT (e.g. older Hibernate update).
ALTER TABLE reservations MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
