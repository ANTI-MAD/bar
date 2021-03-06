package com.example.Bar.repository;

import com.example.Bar.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    List<OrderEntity> findAllByTimeCloseIsNull();
    Optional<OrderEntity> findByTableNumberAndTimeCloseIsNull(Integer tableNumber);
    List<OrderEntity> findAllByTimeCloseAfter(LocalDateTime localDateTime);
}
