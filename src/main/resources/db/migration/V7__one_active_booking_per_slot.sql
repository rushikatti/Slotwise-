-- only one CONFIRMED booking per slot; cancelled bookings don't count
CREATE UNIQUE INDEX uq_bookings_one_active_per_slot
    ON bookings (slot_id)
    WHERE status = 'CONFIRMED';