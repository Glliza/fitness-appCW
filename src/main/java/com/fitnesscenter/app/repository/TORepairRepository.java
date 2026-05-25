package com.fitnesscenter.app.repository;

import com.fitnesscenter.app.entity.TORepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TORepairRepository extends JpaRepository<TORepair, Long> {
}