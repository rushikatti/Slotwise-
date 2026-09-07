-- Table for AvailabilityRule
CREATE TABLE availability_rules (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES resources(id),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration BIGINT NOT NULL
);

-- Helper table for the Set<DayOfWeek> collection
CREATE TABLE availability_rule_days (
    rule_id BIGINT NOT NULL REFERENCES availability_rules(id),
    day_of_week VARCHAR(20) NOT NULL
);

-- Table for Slot
CREATE TABLE slots (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES resources(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);