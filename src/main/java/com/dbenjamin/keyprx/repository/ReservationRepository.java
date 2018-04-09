package com.dbenjamin.keyprx.repository;

import com.dbenjamin.keyprx.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByEndTimeGreaterThanAndStartTimeLessThanOrderByStartTime(Long startTime, Long endTime);

}
