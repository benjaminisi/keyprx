package com.dbenjamin.keyprx.service;

import com.dbenjamin.keyprx.model.Reservation;
import com.dbenjamin.keyprx.repository.ReservationRepository;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ReservationHelper {

    private static Logger log = Logger.getLogger(ReservationHelper.class.getName());

    private static ReservationHelper selfSingleton = new ReservationHelper();

    private ReservationRepository reservationRepository;

    private ReservationHelper() {
        // only allow the singleton creation
    }

    public static ReservationHelper getInstance() {
        return ReservationHelper.selfSingleton;
    }

    public static ReservationHelper getInstance(ReservationRepository reservationRepository) {
        if (reservationRepository == null) {
            throw new IllegalArgumentException("reservationRepository may not be null");
        }
        ReservationHelper.selfSingleton.reservationRepository = reservationRepository;
        return ReservationHelper.selfSingleton;
    }

    public void setReservationRepository(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Count concurrent reservations.  Return false if it reaches capacity between startTime and EndTime
     * It is up to clients to manage day boundaries and adapt for user-appropriate timezones
     *
     * This helper (repository wrapper) gives the opportunity to validate parameters
     * and to perform caching to reduce the burden on the database at scale.
     * Any cache needs intra-cluster invalidation.
     * The cache could be an internal service such as Redis.
     * The cache could be sharded by time windows (1D equivalent of tiled geo search) to avoid invalidating entirely for every new reservation or cancellation.
     *
     * @param newReservationStartTime Unix Epoch milliseconds
     * @param newReservationEndTime Unix Epoch milliseconds the same as or after startTime
     * @param capacity The maximum allowable concurrent reservations before we give up and return false
     * @return true when capacity is more than the highest concurrent Reservation count between startTime and endTime (not including the endTime itself)
     */
    public Boolean checkHasAvailableCapacity(Long newReservationStartTime, Long newReservationEndTime, Integer capacity) {
        if (newReservationEndTime < newReservationStartTime) {
            throw new IllegalArgumentException("Start time must be before end Time");
        }

        log.info("Checking capacity of " + capacity);

        // implemented by Spring Data as: "SELECT * FROM reservation r WHERE ?1 < r.endTime AND ?2 >= r.startTime"
        List<Reservation> reservations = reservationRepository.findByEndTimeGreaterThanAndStartTimeLessThanOrderByStartTime(newReservationStartTime, newReservationEndTime);
        Iterator<Reservation> reservationsI = reservations.iterator();

        // It is critical that reservationsI is sorted by startTime

        Boolean capacityOkay = 0 < capacity;

        // Do not start checking capacity until startTime and quit checking after endTime
        Long simTime = -1L;

        List<Long> concurrentReservationsEndTimes = new LinkedList<>();  // TODO DPB optimize by keeping this list sorted

        while (capacityOkay && simTime < newReservationEndTime && reservationsI.hasNext()) {

            Reservation reservation = reservationsI.next();
            Long reservationStart = reservation.getStartTime();

            log.info("Before " + reservation.getName() + " concurrent is " + concurrentReservationsEndTimes.size());

            // find all were-concurrent reservations that ended before reservationTime
            List<Long> removeTheseConcurrentReservationsEndTimes = new LinkedList<>();
            for (Long endTime : concurrentReservationsEndTimes) {
                if (endTime <= reservationStart) {
                    removeTheseConcurrentReservationsEndTimes.add(endTime);
                }
            }
            concurrentReservationsEndTimes.removeAll(removeTheseConcurrentReservationsEndTimes);

            // add this reservation to endTimesCurrent
            concurrentReservationsEndTimes.add(reservation.getEndTime());

            log.info("After " + reservation.getName() + " concurrent is " + concurrentReservationsEndTimes.size() + " after " + removeTheseConcurrentReservationsEndTimes.size() + " ended");

            // set up for next iteration of the while
            simTime = reservationStart;
            if (simTime > newReservationStartTime) { // don't check capacity until we get into the reservation time
                capacityOkay = concurrentReservationsEndTimes.size() < capacity; // TODO DPB change this to <= depending on semantics of capacity
            }
        }

        // need to check again in case the sim time never got past the newReservationStartTime
        capacityOkay = concurrentReservationsEndTimes.size() < capacity; // TODO DPB change this to <= depending on semantics of capacity

        log.info("Capacity check ending concurrency " + concurrentReservationsEndTimes.size() + " is " + capacityOkay);

        return capacityOkay;
    };


}












