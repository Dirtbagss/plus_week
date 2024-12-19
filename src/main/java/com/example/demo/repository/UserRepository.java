package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    List<User> findAllByIdIn(List<Long> userIds);

    default User findUserById(Long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 데이터가 존재하지 않습니다."));
    }

    @Modifying
    @Query("UPDATE User u SET u.status = 'BLOCKED' WHERE u.id IN :userIds")
    void updateUserStatusBlock(List<Long> userIds);
}
