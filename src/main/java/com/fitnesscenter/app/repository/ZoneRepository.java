package com.fitnesscenter.app.repository;

import com.fitnesscenter.app.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByIdAndDeletedFalse(Long id);
    List<Zone> findAllByDeletedFalse();
    Page<Zone> findAllByDeletedFalse(Pageable pageable);  // НОВЫЙ МЕТОД
    boolean existsByNameAndDeletedFalse(String name);
    boolean existsByIdAndDeletedFalse(Long id);
}