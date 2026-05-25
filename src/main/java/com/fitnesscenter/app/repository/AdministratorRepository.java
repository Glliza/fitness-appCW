package com.fitnesscenter.app.repository;

import com.fitnesscenter.app.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByLoginAndDeletedFalse(String login);
}