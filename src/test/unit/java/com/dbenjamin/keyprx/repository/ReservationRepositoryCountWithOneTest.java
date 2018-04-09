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
import static org.junit.Assert.assertFalse;

/**
 * Test when there is one reservation in the database
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationRepositoryCountWithOneTest {

    private static final Long now = System.currentTimeMillis();

    private static final Long DAYS = 24L * 60L * 60L * 1000L;

    private static final Long START_TIME_3_DAYS = now + 3L * DAYS;
    private static final Long END_TIME_6_DAYS = now + 6L * DAYS;

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
        reservation1.setStartTime(START_TIME_3_DAYS);
        reservation1.setEndTime(END_TIME_6_DAYS);
        reservationRepository.save(reservation1);
    }

    @Test
    public void span() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_2_DAYS, TIME_7_DAYS, 1));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_2_DAYS, TIME_7_DAYS, 2));
    }

    @Test
    public void contains() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_4_DAYS, TIME_5_DAYS, 1));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_4_DAYS, TIME_5_DAYS, 2));
    }

    @Test
    public void exactMatch() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 1));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 2));
    }

    @Test
    public void beforeToEnd() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 1));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 2));
    }

    @Test
    public void startToAfter() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 1));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 2));
    }

    @Test
    public void beforeToMid() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 1));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 2));
    }

    @Test
    public void midToAfter() {
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 1));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_3_DAYS, TIME_6_DAYS, 2));
    }

}
