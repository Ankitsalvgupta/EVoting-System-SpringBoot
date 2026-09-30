package com.codingninjas.EVotingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.codingninjas.EVotingSystem.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByName(String name);

    boolean existsByName(String name);
}
