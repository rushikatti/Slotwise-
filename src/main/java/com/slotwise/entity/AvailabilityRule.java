package com.slotwise.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "availability_rules")
@Data
public class AvailabilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @ElementCollection(targetClass = DayOfWeek.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "availability_rule_days", joinColumns = @JoinColumn(name = "rule_id"))
    @Column(name = "day_of_week")
    private Set<DayOfWeek> daysOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private Duration slotDuration;
}