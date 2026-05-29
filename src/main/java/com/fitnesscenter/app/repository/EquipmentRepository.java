package com.fitnesscenter.app.repository;

import com.fitnesscenter.app.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByIdAndDeletedFalse(Long id);
    List<Equipment> findAllByDeletedFalse();
    Page<Equipment> findAllByDeletedFalse(Pageable pageable);  // НОВЫЙ МЕТОД
    List<Equipment> findByZoneIdAndDeletedFalse(Long zoneId);
    List<Equipment> findByStatusAndDeletedFalse(String status);
    Integer countByZoneIdAndDeletedFalse(Long zoneId);

    // Методы для фильтрации с пагинацией
    Page<Equipment> findByZoneIdAndDeletedFalse(Long zoneId, Pageable pageable);
    Page<Equipment> findByStatusAndDeletedFalse(String status, Pageable pageable);
    Page<Equipment> findByZoneIdAndStatusAndDeletedFalse(Long zoneId, String status, Pageable pageable);
}