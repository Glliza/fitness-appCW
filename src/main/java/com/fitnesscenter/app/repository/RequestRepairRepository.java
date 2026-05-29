package com.fitnesscenter.app.repository;

import com.fitnesscenter.app.entity.RequestRepair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RequestRepairRepository extends JpaRepository<RequestRepair, Long> {
    List<RequestRepair> findByEquipmentInventoryNumber(Long equipmentId);
    List<RequestRepair> findByStatus(String status);
    Page<RequestRepair> findAll(Pageable pageable);
}