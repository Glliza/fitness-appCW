package com.fitnesscenter.app.repository;


import com.fitnesscenter.app.entity.Inventarization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventarizationRepository extends JpaRepository<Inventarization, Long> {
    List<Inventarization> findByEquipmentInventoryNumber(Long equipmentId);
}