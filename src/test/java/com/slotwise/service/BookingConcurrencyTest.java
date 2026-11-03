package com.slotwise.service;

import com.slotwise.entity.BookingStatus;
import com.slotwise.entity.Resource;
import com.slotwise.entity.Slot;
import com.slotwise.entity.SlotStatus;
import com.slotwise.entity.Tenant;
import com.slotwise.entity.User;
import com.slotwise.exception.ConflictException;
import com.slotwise.repository.BookingRepository;
import com.slotwise.repository.ResourceRepository;
import com.slotwise.repository.SlotRepository;
import com.slotwise.repository.TenantRepository;
import com.slotwise.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingConcurrencyTest {

    private static final int THREADS = 20;

    @Autowired private BookingService bookingService;
    @Autowired private TenantRepository tenantRepository;
    @Autowired private ResourceRepository resourceRepository;
    @Autowired private SlotRepository slotRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookingRepository bookingRepository;

    private Long tenantId;
    private Long resourceId;
    private Long slotId;
    private final List<Long> userIds = new ArrayList<>();

    @BeforeEach
    void createTestData() {
        long stamp = System.nanoTime();

        Tenant tenant = new Tenant();
        tenant.setName("Race Test " + stamp);
        tenant.setSlug("race-test-" + stamp);
        tenant = tenantRepository.save(tenant);
        tenantId = tenant.getId();

        Resource resource = new Resource();
        resource.setName("Race Resource");
        resource.setType("ROOM");
        resource.setTenant(tenant);
        resource = resourceRepository.save(resource);
        resourceId = resource.getId();

        Slot slot = new Slot();
        slot.setResource(resource);
        slot.setStartTime(LocalDateTime.now().plusDays(1));
        slot.setEndTime(LocalDateTime.now().plusDays(1).plusMinutes(30));
        slot.setStatus(SlotStatus.AVAILABLE);
        slotId = slotRepository.save(slot).getId();

        for (int i = 0; i < THREADS; i++) {
            User user = new User();
            user.setName("Racer " + i);
            user.setEmail("racer" + i + "-" + stamp + "@test.com");
            user.setPassword("not-a-real-hash");
            user.setRole(User.Role.CUSTOMER);
            user.setTenant(tenant);
            userIds.add(userRepository.save(user).getId());
        }
    }

    @AfterEach
    void removeTestData() {
        bookingRepository.deleteAll(bookingRepository.findAll().stream()
                .filter(b -> b.getSlot().getId().equals(slotId))
                .toList());
        slotRepository.deleteById(slotId);
        resourceRepository.deleteById(resourceId);
        userRepository.deleteAllById(userIds);
        tenantRepository.deleteById(tenantId);
    }

    @Test
    void onlyOneCustomerCanBookTheSameSlot() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch ready = new CountDownLatch(THREADS);   // everyone is in position
        CountDownLatch go = new CountDownLatch(1);            // the starting gun

        AtomicInteger success = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();   // lost the race, got a clean conflict
        AtomicInteger other = new AtomicInteger();      // anything unexpected

        List<Future<?>> futures = new ArrayList<>();
        for (Long userId : userIds) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                try {
                    go.await();
                    bookingService.create(userId, tenantId, slotId);
                    success.incrementAndGet();
                } catch (ConflictException | OptimisticLockingFailureException e) {
                    rejected.incrementAndGet();
                } catch (Exception e) {
                    other.incrementAndGet();
                    e.printStackTrace();
                }
            }));
        }

        ready.await();
        go.countDown();
        for (Future<?> f : futures) {
            f.get(30, TimeUnit.SECONDS);
        }
        pool.shutdown();

        System.out.println("RESULT success=" + success + " rejected=" + rejected + " other=" + other);

        assertEquals(1, success.get(), "exactly one request may succeed");
        assertEquals(THREADS - 1, rejected.get(), "all the others must be cleanly rejected");
        assertEquals(0, other.get(), "no unexpected errors");
        assertEquals(1, bookingRepository.countBySlotIdAndStatus(slotId, BookingStatus.CONFIRMED),
                "exactly one CONFIRMED booking in the database");
        assertEquals(SlotStatus.BOOKED, slotRepository.findById(slotId).orElseThrow().getStatus());
    }
}