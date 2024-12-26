package com.example.demo.controller;

import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.ok(reservationService.createReservation(reservationRequestDto.getItemId(),
                                            reservationRequestDto.getUserId(),
                                            reservationRequestDto.getStartAt(),
                                            reservationRequestDto.getEndAt()));
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<ReservationResponseDto> updateReservation(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }

    @GetMapping
    public void findAll() {
        reservationService.getReservations();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponseDto>> searchAll(@RequestParam(required = false) Long userId,
                                                                  @RequestParam(required = false) Long itemId) {
        return ResponseEntity.ok(reservationService.searchAndConvertReservations(userId, itemId));
    }
}
