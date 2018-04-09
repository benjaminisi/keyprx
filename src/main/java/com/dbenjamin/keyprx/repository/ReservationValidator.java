package com.dbenjamin.keyprx.repository;

import com.dbenjamin.keyprx.model.Block;
import com.dbenjamin.keyprx.model.Reservation;
import com.dbenjamin.keyprx.service.ReservationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("beforeCreateReservationValidator") // TODO DPB add as listener to also validate when updating an existing reservation
public class ReservationValidator implements Validator {

    private static final Long DAYS = 24L * 60L * 60L * 1000L;
    private static final Long TOO_FAR_IN_THE_FUTURE = 50L * 365L * DAYS; // Sanity check of 50 years out TODO externally configure

    private BlockRepository blockRepository;

    private ReservationHelper reservationHelper = ReservationHelper.getInstance();

    @Autowired
    public ReservationValidator(BlockRepository blockRepository, ReservationRepository reservationRepository) {
        this.blockRepository = blockRepository;
        this.reservationHelper.setReservationRepository(reservationRepository); // TODO DPB improve DI/IOC design
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Reservation.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Reservation reservation = (Reservation) obj;

        if (reservation.getStartTime() == null) {
            errors.rejectValue("startTime", "startTime.missing", "Add your start time as Unix Epoch Milliseconds");
        } else if (isTimeInsane(reservation.getStartTime())) {
            errors.rejectValue("startTime", "startTime.outOfBounds", "Your start time needs to be in the not-too-distant future");
        }

        if (reservation.getEndTime() == null) {
            errors.rejectValue("endTime", "endTime.missing", "Add your end time as Unix Epoch Milliseconds");
        } else if (isTimeInsane(reservation.getEndTime())) {
            errors.rejectValue("endTime", "endTime.outOfBounds", "Your end time needs to be in the not-too-distant future");
        }

        if (errors.hasErrors()) {
            return;
        }

        if (reservation.getStartTime() > reservation.getEndTime()) {
            errors.rejectValue("endTime", "endTime.lessThanStartTime", "Your start time needs to be before your end time");
            return;
        }

        if (errors.hasErrors()) {
            return;
        }

        Block block = fetchBlock();
        if (block == null) {
            errors.reject("400", null, "Register a block to be reserved with POST /api/blocks"); // BAD SYSTEM STATE -- No reservable blocks
        } else {
            Integer overbookedCapacity = block.computeOverbookedCapacity();
            if (!checkHasAvailableCapacity(reservation, overbookedCapacity)) {
                errors.rejectValue("startTime", "startTime.unavailable", "We are already at capacity of " + overbookedCapacity);
            }
        }
    }

    private Boolean checkHasAvailableCapacity(Reservation reservation, Integer capacity) {
        return reservationHelper.checkHasAvailableCapacity(reservation.getStartTime(), reservation.getEndTime(), capacity);
    }

    private Block fetchBlock() {
        Iterable<Block> blocks = blockRepository.findAll();
        return blocks != null && blocks.iterator().hasNext() ? blocks.iterator().next() : null;
    }

    private boolean isTimeInsane(Long input) {
        Long now = System.currentTimeMillis();
        return (input < now || input > now + TOO_FAR_IN_THE_FUTURE);
    }
}