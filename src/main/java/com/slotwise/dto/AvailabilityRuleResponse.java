package com.slotwise.dto;

import com.slotwise.entity.AvailabilityRule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.TreeSet;

public record AvailabilityRuleResponse(Long id, Set<DayOfWeek> daysOfWeek,
                                       LocalTime startTime, LocalTime endTime, long slotMinutes) {
    public static AvailabilityRuleResponse from(AvailabilityRule r) {
        return new AvailabilityRuleResponse(r.getId(), new TreeSet<>(r.getDaysOfWeek()),
                r.getStartTime(), r.getEndTime(), r.getSlotDuration().toMinutes());
    }
}