package com.fitnesscenter.app.repository;


import com.fitnesscenter.app.entity.RequestBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestBuyRepository extends JpaRepository<RequestBuy, Long> {
}