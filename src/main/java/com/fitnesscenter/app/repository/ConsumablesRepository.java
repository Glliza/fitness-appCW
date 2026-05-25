package com.fitnesscenter.app.repository;


import com.fitnesscenter.app.entity.Consumables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumablesRepository extends JpaRepository<Consumables, Long> {
}