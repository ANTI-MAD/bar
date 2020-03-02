package com.example.Bar.repository;

import com.example.Bar.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Integer> {

    List<InventoryEntity> findAllByOrderByCategory();
    List<InventoryEntity> findAllByCategoryAndExistIsTrue(String category);

    Optional<InventoryEntity> findByCategory(String category);
}
