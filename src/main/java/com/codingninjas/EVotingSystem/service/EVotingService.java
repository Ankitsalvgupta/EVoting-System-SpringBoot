package com.codingninjas.EVotingSystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingninjas.EVotingSystem.entity.Election;
import com.codingninjas.EVotingSystem.entity.ElectionChoice;
import com.codingninjas.EVotingSystem.entity.User;
import com.codingninjas.EVotingSystem.entity.Vote;
import com.codingninjas.EVotingSystem.repository.ElectionChoiceRepository;
import com.codingninjas.EVotingSystem.repository.ElectionRepository;
import com.codingninjas.EVotingSystem.repository.UserRepository;
import com.codingninjas.EVotingSystem.repository.VoteRepository;
import com.codingninjas.EVotingSystem.exception.DuplicateVoteException;
import com.codingninjas.EVotingSystem.exception.ResourceNotFoundException;

@Service
public class EVotingService {

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ElectionRepository electionRepository;

    @Autowired
    ElectionChoiceRepository electionChoiceRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addElection(Election election) {
        electionRepository.save(election);
    }

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Election findElectionByName(String electionName) {
        return electionRepository.findByName(electionName)
                .orElseThrow(() -> new RuntimeException("Election not found with name: " + electionName));
    }

    public void addElectionChoice(ElectionChoice electionChoice) {
        if (electionChoice.getElection() == null) {
            throw new IllegalArgumentException("election.id is required");
        }
        long electionId = electionChoice.getElection().getId();
        Election managedElection = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found with id: " + electionId));
        electionChoice.setElection(managedElection);
        electionChoiceRepository.save(electionChoice);
    }

    public List<ElectionChoice> getAllElectionChoices() {
        return electionChoiceRepository.findAll();
    }

    public long choicesByElection(Long electionId) {
        return electionChoiceRepository.countByElectionId(electionId);
    }

    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }

    public boolean AlreadyGivenVote(Long userId, Long electionId) {
        return voteRepository.existsByUserIdAndElectionId(userId, electionId);
    }

    public User findUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("User not found: " + name));
    }

    @Transactional
    public void addVote(Long userId, Long electionId, Long electionChoiceId) {
        if (AlreadyGivenVote(userId, electionId)) {
            throw new DuplicateVoteException("You have already voted in this election");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));

        ElectionChoice electionChoice = electionChoiceRepository.findById(electionChoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("ElectionChoice not found with id: " + electionChoiceId));

        // Integrity check: the chosen candidate must belong to the election being voted in
        if (electionChoice.getElection() == null
                || electionChoice.getElection().getId() != election.getId()) {
            throw new IllegalArgumentException(
                    "Choice " + electionChoiceId + " does not belong to election " + electionId);
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setElection(election);
        vote.setElectionChoice(electionChoice);
        voteRepository.save(vote);
    }

    public long countTotalVotes() {
        return voteRepository.count();
    }

    public long countVotesByElectionName(String electionName) {
        Election election = findElectionByName(electionName);
        return voteRepository.countByElectionId(election.getId());
    }

    public ElectionChoice findElectionWinner(String electionName) {
        Election election = findElectionByName(electionName);
        ElectionChoice winner = electionChoiceRepository.findElectionChoiceWithMaxVotes(election.getId());
        if (winner == null) {
            throw new ResourceNotFoundException("No votes have been cast yet in election: " + electionName);
        }
        return winner;
    }
}