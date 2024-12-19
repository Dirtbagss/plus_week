package com.example.demo.entity;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("pending"),
    APPROVED("approved"),
    CANCELED("canceled"),
    EXPIRED("expired");

    private final String name;

    ReservationStatus(String name) {
        this.name = name;
    }

    public static ReservationStatus of(String reservationName) {
        for (ReservationStatus reservationStatus : values()) {
            if (reservationStatus.getName().equals(reservationName)) {
                return reservationStatus;
            }
        }

        throw new IllegalArgumentException("해당하는 이름의 권한을 찾을 수 없습니다: " + reservationName);
    }
}
