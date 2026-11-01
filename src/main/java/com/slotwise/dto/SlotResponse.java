package com.slotwise.dto;

import com.slotwise.entity.Slot;

import java.time.LocalDateTime;

public record SlotResponse(Long id, LocalDateTime startTime, LocalDateTime endTime, String status) {
    public static SlotResponse from(Slot s) {
        return new SlotResponse(s.getId(), s.getStartTime(), s.getEndTime(), s.getStatus().name());
    }
}