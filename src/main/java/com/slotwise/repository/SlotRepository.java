package com.slotwise.repository;

import com.slotwise.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    // all slots of one resource that start inside [from, to)
    @Query("select s from Slot s where s.resource.id = :resourceId "
            + "and s.startTime >= :from and s.startTime < :to order by s.startTime")
    List<Slot> findInRange(@Param("resourceId") Long resourceId,
                           @Param("from") LocalDateTime from,
                           @Param("to") LocalDateTime to);
}