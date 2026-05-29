package com.fitnesscenter.app.repository;

import com.fitnesscenter.app.entity.TORepair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TORepairRepository extends JpaRepository<TORepair, Long> {
    List<TORepair> findByEquipmentIdAndStatus(Long equipmentId, String status);
    Page<TORepair> findAll(Pageable pageable);
}