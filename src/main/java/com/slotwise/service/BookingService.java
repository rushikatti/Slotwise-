package com.slotwise.service;

import com.slotwise.dto.BookingResponse;
import com.slotwise.dto.PagedResponse;
import com.slotwise.entity.Booking;
import com.slotwise.entity.BookingStatus;
import com.slotwise.entity.Slot;
import com.slotwise.entity.SlotStatus;
import com.slotwise.exception.BadRequestException;
import com.slotwise.exception.ConflictException;
import com.slotwise.exception.ResourceNotFoundException;
import com.slotwise.repository.BookingRepository;
import com.slotwise.repository.SlotRepository;
import com.slotwise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse create(Long userId, Long tenantId, Long slotId) {
        // slot must belong to the caller's tenant, otherwise it looks like it doesn't exist
        Slot slot = slotRepository.findByIdAndResourceTenantId(slotId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot book a slot in the past");
        }
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new ConflictException("Slot is not available");
        }

        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.saveAndFlush(slot);   // @Version on Slot is checked here

        Booking booking = new Booking();
        booking.setSlot(slot);
        booking.setUser(userRepository.getReferenceById(userId));
        booking.setStatus(BookingStatus.CONFIRMED);
        return BookingResponse.from(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public PagedResponse<BookingResponse> listMine(Long userId, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
        return PagedResponse.from(
                bookingRepository.findByUserId(userId, pageable).map(BookingResponse::from));
    }

    @Transactional
    public BookingResponse cancel(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Slot slot = booking.getSlot();
        slot.setStatus(SlotStatus.AVAILABLE);   // frees the slot again
        slotRepository.saveAndFlush(slot);

        return BookingResponse.from(booking);
    }
}