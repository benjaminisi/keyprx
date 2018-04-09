package com.dbenjamin.keyprx.repository;

import com.dbenjamin.keyprx.model.Block;
import com.dbenjamin.keyprx.model.Reservation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.Errors;

import java.util.ArrayList;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationValidatorTest {

    private static final Long now = System.currentTimeMillis();
    private static final Long DAYS = 24L * 60L * 60L * 1000L;

    private static final Long START_TIME_1 = now + DAYS;
    private static final Long END_TIME_1 = now + 2L * DAYS;

    private static final Long TOO_FAR_IN_THE_FUTURE = now + (50L +1L) * 365L * DAYS; // Sanity check of 50 years out TODO externally configure

    @MockBean
    private BlockRepository blockRepository;

    @MockBean
    private ReservationRepository reservationRepository;

    private ReservationValidator reservationValidator;

    private Reservation reservation1 = new Reservation();

    @Before
    public void setUp() {
        Block hotelDavid = new Block();
        hotelDavid.setName("Hotel David");
        hotelDavid.setCapacity(1);
        hotelDavid.setOverbookPercent(0);

        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(hotelDavid);

        when(blockRepository.findAll())
                .thenReturn(blocks);

        reservation1.setEmail("Janeth@example.com");
        reservation1.setName("Janeth Espinosa");
        reservation1.setStartTime(START_TIME_1);
        reservation1.setEndTime(END_TIME_1);

        ArrayList<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1);

        reservationValidator = new ReservationValidator(blockRepository, reservationRepository);

        when(reservationRepository.findByEndTimeGreaterThanAndStartTimeLessThanOrderByStartTime(START_TIME_1, END_TIME_1))
                .thenReturn(reservations)
                .thenReturn(reservations);
    }

    @Mock
    private Errors errors;


    @Test
    public void validateRejectsMissingStartTime() {
        reservation1.setStartTime(null);
        when(errors.hasErrors()).thenReturn(true);
        reservationValidator.validate(reservation1, errors);
        verify(errors).rejectValue("startTime", "startTime.missing", "Add your start time as Unix Epoch Milliseconds");
    }

    @Test
    public void validateRejectsStartTimeInThePast() {
        reservation1.setStartTime(999999L);
        when(errors.hasErrors()).thenReturn(true);
        reservationValidator.validate(reservation1, errors);
        verify(errors).rejectValue("startTime", "startTime.outOfBounds", "Your start time needs to be in the not-too-distant future");
    }

    @Test
    public void validationRejectsFarFutureStartTime() {
        reservation1.setStartTime(TOO_FAR_IN_THE_FUTURE);
        reservation1.setEndTime(TOO_FAR_IN_THE_FUTURE + 9999L);
        when(errors.hasErrors()).thenReturn(true);
        reservationValidator.validate(reservation1, errors);
        InOrder inOrder = inOrder(errors, errors);
        inOrder.verify(errors).rejectValue("startTime", "startTime.outOfBounds", "Your start time needs to be in the not-too-distant future");
        inOrder.verify(errors).rejectValue("endTime", "endTime.outOfBounds", "Your end time needs to be in the not-too-distant future");
    }

    @Test
    public void validateRejectsMissingEndTime() {
        reservation1.setEndTime(null);
        when(errors.hasErrors()).thenReturn(true);
        reservationValidator.validate(reservation1, errors);
        verify(errors).rejectValue("endTime", "endTime.missing", "Add your end time as Unix Epoch Milliseconds");
    }

    @Test
    public void validateRejectsEndTimeInThePast() {
        reservation1.setEndTime(99999L);
        when(errors.hasErrors()).thenReturn(true);
        reservationValidator.validate(reservation1, errors);
        verify(errors).rejectValue("endTime", "endTime.outOfBounds", "Your end time needs to be in the not-too-distant future");
    }

    @Test
    public void validationRejectsFarFutureEndTime() {
        reservation1.setEndTime(TOO_FAR_IN_THE_FUTURE);
        when(errors.hasErrors()).thenReturn(true);
        reservationValidator.validate(reservation1, errors);
        verify(errors).rejectValue("endTime", "endTime.outOfBounds", "Your end time needs to be in the not-too-distant future");
    }

    @Test
    public void validateRejectsEndTimeBeforeStartTime() {
        reservation1.setStartTime(END_TIME_1);
        reservation1.setEndTime(START_TIME_1);
        when(errors.hasErrors()).thenReturn(false).thenReturn(true);
        reservationValidator.validate(reservation1, errors);
        verify(errors).rejectValue("endTime", "endTime.lessThanStartTime", "Your start time needs to be before your end time");
    }

    @Test
    public void validateAcceptsWhenThereIsUnusedCapacity() {
        when(errors.hasErrors()).thenReturn(false).thenReturn(false);
        reservationValidator.validate(reservation1, errors);
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
    }

}