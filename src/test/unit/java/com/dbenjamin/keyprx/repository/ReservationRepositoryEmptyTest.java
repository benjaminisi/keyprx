package com.dbenjamin.keyprx.repository;

import com.dbenjamin.keyprx.service.ReservationHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationRepositoryEmptyTest {

    private static final Long now = System.currentTimeMillis();

    private static final Long DAYS = 24L * 60L * 60L * 1000L;

    private static final Long TIME_2_DAYS = now + 2L * DAYS;
    private static final Long TIME_7_DAYS = now + 7L * DAYS;


    @Autowired
    private ReservationRepository reservationRepository;

    private ReservationHelper reservationHelper = ReservationHelper.getInstance();

    @Test
    public void rejectsAllForCapacityZero() {
        reservationHelper.setReservationRepository(reservationRepository);
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_2_DAYS, TIME_7_DAYS, 0));
        assertTrue(reservationHelper.checkHasAvailableCapacity(TIME_2_DAYS, TIME_7_DAYS, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsForReversedArguments() {
        reservationHelper.setReservationRepository(reservationRepository);
        assertFalse(reservationHelper.checkHasAvailableCapacity(TIME_7_DAYS, TIME_2_DAYS, 1));
    }

}
