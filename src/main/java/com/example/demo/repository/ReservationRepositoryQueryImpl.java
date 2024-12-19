package com.example.demo.repository;

import com.example.demo.entity.QItem;
import com.example.demo.entity.QReservation;
import com.example.demo.entity.QUser;
import com.example.demo.entity.Reservation;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReservationRepositoryQueryImpl implements ReservationRepositoryQuery {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Reservation> searchReservations(Long userId, Long itemId) {
        JPAQuery<Reservation> query = new JPAQuery<>(entityManager);

        QReservation reservation = QReservation.reservation;
        QUser user = QUser.user;
        QItem item = QItem.item;

        // 기본 쿼리 생성
        query.select(reservation)
                .from(reservation)
                .leftJoin(reservation.user, user).fetchJoin()  // N+1 문제 해결
                .leftJoin(reservation.item, item).fetchJoin(); // N+1 문제 해결

        // 동적 조건 추가
        if (userId != null) {
            query.where(reservation.user.id.eq(userId));
        }
        if (itemId != null) {
            query.where(reservation.item.id.eq(itemId));
        }

        return query.fetch();
    }
}
