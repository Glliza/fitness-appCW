package com.fitnesscenter.app.repository;


import com.fitnesscenter.app.entity.ConsumablesZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConsumablesZoneRepository extends JpaRepository<ConsumablesZone, Long> {
    Optional<ConsumablesZone> findByConsumablesIdAndZoneId(Long consumablesId, Long zoneId);
}
