package com.codingninjas.EVotingSystem.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.codingninjas.EVotingSystem.entity.Election;

public interface ElectionRepository extends JpaRepository<Election, Long> {

    Optional<Election> findByName(String name);
}
