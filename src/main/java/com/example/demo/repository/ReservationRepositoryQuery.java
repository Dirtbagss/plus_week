package com.example.demo.repository;

import com.example.demo.entity.Reservation;

import java.util.List;

public interface ReservationRepositoryQuery {

    List<Reservation> searchReservations(Long userId, Long itemId);

}
