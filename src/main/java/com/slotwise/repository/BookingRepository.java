package com.slotwise.repository;

import com.slotwise.entity.Booking;
import com.slotwise.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    // a booking is only visible to the user who made it
    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    long countBySlotIdAndStatus(Long slotId, BookingStatus status);
}