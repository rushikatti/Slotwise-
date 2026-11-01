ALTER TABLE slots
    ADD CONSTRAINT uq_slots_resource_start UNIQUE (resource_id, start_time);