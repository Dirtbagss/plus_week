package com.example.demo.service;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.*;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;

    public ReservationService(ReservationRepository reservationRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              RentalLogService rentalLogService) {
        this.reservationRepository = reservationRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.rentalLogService = rentalLogService;
    }

    // TODO: 1. 트랜잭션 이해
    @Transactional
    public void createReservation(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        // 쉽게 데이터를 생성하려면 아래 유효성검사 주석 처리
        List<Reservation> haveReservations = reservationRepository.findConflictingReservations(itemId, startAt, endAt);
        if(!haveReservations.isEmpty()) {
            throw new ReservationConflictException("해당 물건은 이미 그 시간에 예약이 있습니다.");
        }

        Item item = itemRepository.findItemById(itemId);
        User user = userRepository.findUserById(userId);
        Reservation reservation = new Reservation(item, user, Status.PENDING, startAt, endAt);
        Reservation savedReservation = reservationRepository.save(reservation);

        RentalLog rentalLog = new RentalLog(savedReservation, "로그 메세지", "CREATE");
        rentalLogService.save(rentalLog);
    }

    // TODO: 3. N+1 문제
    public List<ReservationResponseDto> getReservations() {
        List<Reservation> reservations = reservationRepository.findAllWithItemAndUser();
        return convertToDto(reservations);

//        return reservations.stream().map(reservation -> {
//            User user = reservation.getUser();
//            Item item = reservation.getItem();
//
//            return new ReservationResponseDto(
//                    reservation.getId(),
//                    user.getNickname(),
//                    item.getName(),
//                    reservation.getStartAt(),
//                    reservation.getEndAt()
//            );
//        }).toList();
    }

    // TODO: 5. QueryDSL 검색 개선
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {

        List<Reservation> reservations = reservationRepository.searchReservations(userId, itemId);

        return convertToDto(reservations);
    }


    private List<ReservationResponseDto> convertToDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUser().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                ))
                .toList();
    }

    // TODO: 7. 리팩토링
    @Transactional
    public ReservationResponseDto updateReservationStatus(Long reservationId, String status) {
        Reservation reservation = reservationRepository.findReservationById(reservationId);

        Status reservationStatus = reservation.getStatus();

        if ("APPROVED".equals(status) && !Status.PENDING.equals(reservationStatus)) {
            throw new IllegalArgumentException("PENDING 상태만 APPROVED로 변경 가능합니다.");
        }

        if ("BLOCKED".equals(status) && Status.EXPIRED.equals(reservationStatus)) {
            throw new IllegalArgumentException("EXPIRED 상태인 예약은 취소할 수 없습니다.");
        }

        if ("EXPIRED".equals(status) && !Status.PENDING.equals(reservationStatus)) {
            throw new IllegalArgumentException("PENDING 상태만 EXPIRED로 변경 가능합니다.");
        }

        reservation.updateStatus(Status.valueOf(status));

        return new ReservationResponseDto(reservation.getId(),
                reservation.getUser().getNickname(),
                reservation.getItem().getName(),
                reservation.getStartAt(),
                reservation.getEndAt());
    }
}


