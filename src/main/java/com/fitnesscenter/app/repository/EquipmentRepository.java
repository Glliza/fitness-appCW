package com.fitnesscenter.app.repository;

import com.fitnesscenter.app.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByIdAndDeletedFalse(Long id);
    List<Equipment> findAllByDeletedFalse();
    List<Equipment> findByZoneIdAndDeletedFalse(Long zoneId);
    List<Equipment> findByStatusAndDeletedFalse(String status);
    Integer countByZoneIdAndDeletedFalse(Long zoneId);
}