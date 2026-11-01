package com.slotwise.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record AvailabilityRuleRequest(
        @NotEmpty(message = "At least one day is required") Set<DayOfWeek> daysOfWeek,
        @NotNull(message = "startTime is required") LocalTime startTime,
        @NotNull(message = "endTime is required") LocalTime endTime,
        @Min(value = 5, message = "slotMinutes must be at least 5")
        @Max(value = 480, message = "slotMinutes must be at most 480") int slotMinutes) {}