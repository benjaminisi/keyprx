package com.dbenjamin.keyprx.repository;

import com.dbenjamin.keyprx.model.Reservation;
import com.dbenjamin.keyprx.service.ReservationHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test when there is one reservation in the database
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationRepositoryCountWithManyTest {

    private static final Long now = System.currentTimeMillis();

    private static final Long DAYS = 24L * 60L * 60L * 1000L;

    private static final Long TIME_1_DAY = now + 1L * DAYS;
    private static final Long TIME_2_DAYS = now + 2L * DAYS;
    private static final Long TIME_3_DAYS = now + 3L * DAYS;
    private static final Long TIME_4_DAYS = now + 4L * DAYS;
    private static final Long TIME_5_DAYS = now + 5L * DAYS;
    private static final Long TIME_6_DAYS = now + 6L * DAYS;
    private static final Long TIME_7_DAYS = now + 7L * DAYS;

    @Autowired
    private ReservationRepository reservationRepository;
    
    private ReservationHelper reservationHelper = ReservationHelper.getInstance();

    @Before
    public void setUp() {
        reservationHelper.setReservationRepository(reservationRepository);
        // Test harness rolls back DB after each test
        Reservation reservation1 = new Reservation();
        reservation1.setEmail("Janeth@example.com");
        reservation1.setName("Janeth Espinosa");
        reservation1.setStartTime(TIME_2_DAYS);
        reservation1.setEndTime(TIME_3_DAYS);
        reservationRepository.save(reservation1);
        Reservation reservation2 = new Reservation();
        reservation2.setEmail("Jorge@example.com");
        reservation2.setName("Jorge Kwan");
        reservation2.setStartTime(TIME_3_DAYS);
        reservation2.setEndTime(TIME_5_DAYS);
        reservationRepository.save(reservation2);
        Reservation reservation3 = new Reservation();
        reservation3.setEmail("Kai@example.com");
        reservation3.setName("Kai Rosen");
        reservation3.setStartTime(TIME_4_DAYS);
        reservation3.setEndTime(TIME_5_DAYS);
        reservationRepository.save(reservation3);
        Reservation reservation4 = new Reservation();
        reservation4.setEmail("Lupita@example.com");
        reservation4.setName("Lupita Orinoco");
        reservation4.setStartTime(TIME_3_DAYS);
        reservation4.setEndTime(TIME_6_DAYS);
        reservationRepository.save(reservation4);
        reservationRepository.flush();
        System.out.println("reservation count: " + reservationRepository.findAll().size());
    }

    @Test
    public void completeOverlap() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_1_DAY, TIME_7_DAYS, 3));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_1_DAY, TIME_7_DAYS, 4));
    }

    @Test
    public void singleWithAnotherStartingAtEnd() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_2_DAYS, TIME_3_DAYS, 1));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_2_DAYS, TIME_3_DAYS, 2));
    }

    @Test
    public void singleWithAnotherEndingAtStart() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_5_DAYS, TIME_6_DAYS, 1));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_5_DAYS, TIME_6_DAYS, 2));
    }

    @Test
    public void concurrentStart() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_4_DAYS, 2));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_4_DAYS, 3));
    }

    @Test
    public void concurrentEnd() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_4_DAYS, TIME_5_DAYS, 3));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_4_DAYS, TIME_5_DAYS, 4));
    }

    @Test
    public void zeroEndingAtFirstStart() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_1_DAY, TIME_2_DAYS, 0));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_1_DAY, TIME_2_DAYS, 1));
    }

    @Test
    public void zeroStartingAtLastEnd() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_6_DAYS, TIME_7_DAYS, 0));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_6_DAYS, TIME_7_DAYS, 1));
    }

    @Test
    public void cmiddleToEnd() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_4_DAYS, TIME_7_DAYS, 3));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_4_DAYS, TIME_7_DAYS, 4));
    }

}
