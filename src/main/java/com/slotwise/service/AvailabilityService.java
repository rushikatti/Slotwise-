package com.slotwise.service;

import com.slotwise.dto.AvailabilityRuleRequest;
import com.slotwise.dto.AvailabilityRuleResponse;
import com.slotwise.dto.SlotResponse;
import com.slotwise.entity.AvailabilityRule;
import com.slotwise.entity.Resource;
import com.slotwise.entity.Slot;
import com.slotwise.entity.SlotStatus;
import com.slotwise.exception.BadRequestException;
import com.slotwise.exception.ResourceNotFoundException;
import com.slotwise.repository.AvailabilityRuleRepository;
import com.slotwise.repository.ResourceRepository;
import com.slotwise.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final ResourceRepository resourceRepository;
    private final AvailabilityRuleRepository ruleRepository;
    private final SlotRepository slotRepository;

    // the resource must belong to the caller's tenant, otherwise it looks like it doesn't exist (404)
    private Resource ownedResource(Long resourceId, Long tenantId) {
        return resourceRepository.findByIdAndTenantId(resourceId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    @Transactional
    public AvailabilityRuleResponse addRule(Long resourceId, Long tenantId, AvailabilityRuleRequest req) {
        Resource resource = ownedResource(resourceId, tenantId);

        if (!req.endTime().isAfter(req.startTime())) {
            throw new BadRequestException("endTime must be after startTime");
        }
        Duration slotDuration = Duration.ofMinutes(req.slotMinutes());
        if (Duration.between(req.startTime(), req.endTime()).compareTo(slotDuration) < 0) {
            throw new BadRequestException("Slot length is longer than the time window");
        }

        AvailabilityRule rule = new AvailabilityRule();
        rule.setResource(resource);
        rule.setDaysOfWeek(new HashSet<>(req.daysOfWeek()));
        rule.setStartTime(req.startTime());
        rule.setEndTime(req.endTime());
        rule.setSlotDuration(slotDuration);
        return AvailabilityRuleResponse.from(ruleRepository.save(rule));
    }

    @Transactional(readOnly = true)
    public List<AvailabilityRuleResponse> listRules(Long resourceId, Long tenantId) {
        ownedResource(resourceId, tenantId);
        return ruleRepository.findByResourceId(resourceId).stream()
                .map(AvailabilityRuleResponse::from)
                .toList();
    }

    @Transactional
    public int generateSlots(Long resourceId, Long tenantId, LocalDate from, LocalDate to) {
        Resource resource = ownedResource(resourceId, tenantId);

        if (to.isBefore(from) || ChronoUnit.DAYS.between(from, to) > 30) {
            throw new BadRequestException("Date range must be between 1 and 31 days");
        }

        List<AvailabilityRule> rules = ruleRepository.findByResourceId(resourceId);

        // start times that already exist in this range (so running generate twice creates nothing new)
        Set<LocalDateTime> taken = slotRepository
                .findInRange(resourceId, from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                .stream()
                .map(Slot::getStartTime)
                .collect(Collectors.toCollection(HashSet::new));

        List<Slot> toSave = new ArrayList<>();

        for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
            for (AvailabilityRule rule : rules) {
                if (!rule.getDaysOfWeek().contains(day.getDayOfWeek())) {
                    continue;
                }
                Duration length = rule.getSlotDuration();
                LocalDateTime cursor = day.atTime(rule.getStartTime());
                LocalDateTime end = day.atTime(rule.getEndTime());

                while (!cursor.plus(length).isAfter(end)) {
                    if (taken.add(cursor)) {          // false if this start time already exists
                        Slot slot = new Slot();
                        slot.setResource(resource);
                        slot.setStartTime(cursor);
                        slot.setEndTime(cursor.plus(length));
                        slot.setStatus(SlotStatus.AVAILABLE);   // CHECK: use your enum's "free" value
                        toSave.add(slot);
                    }
                    cursor = cursor.plus(length);
                }
            }
        }

        slotRepository.saveAll(toSave);
        return toSave.size();
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> listSlots(Long resourceId, Long tenantId, LocalDate date) {
        ownedResource(resourceId, tenantId);
        return slotRepository.findInRange(resourceId, date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                .stream()
                .map(SlotResponse::from)
                .toList();
    }
}