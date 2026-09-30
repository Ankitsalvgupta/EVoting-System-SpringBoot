package com.codingninjas.EVotingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.codingninjas.EVotingSystem.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByUserIdAndElectionId(Long userId, Long electionId);

    long countByElectionId(Long electionId);
}
