package com.slotwise.dto;

import com.slotwise.entity.Booking;
import com.slotwise.entity.Slot;

import java.time.LocalDateTime;

public record BookingResponse(Long id, Long slotId, Long resourceId, String resourceName,
                              LocalDateTime startTime, LocalDateTime endTime,
                              String status, LocalDateTime createdAt) {
    public static BookingResponse from(Booking b) {
        Slot s = b.getSlot();
        return new BookingResponse(b.getId(), s.getId(), s.getResource().getId(),
                s.getResource().getName(), s.getStartTime(), s.getEndTime(),
                b.getStatus().name(), b.getCreatedAt());
    }
}