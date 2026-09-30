package com.codingninjas.EVotingSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.codingninjas.EVotingSystem.entity.Election;
import com.codingninjas.EVotingSystem.entity.ElectionChoice;
import com.codingninjas.EVotingSystem.entity.User;
import com.codingninjas.EVotingSystem.entity.Vote;
import com.codingninjas.EVotingSystem.service.EVotingService;

@RestController
public class EVotingController {

    @Autowired
    EVotingService eVotingService;

    @GetMapping("/get/users")
    public List<User> getAllUsers() {
        return eVotingService.getAllUsers();
    }

    @PostMapping("/add/election")
    public void addElection(@RequestBody Election election) {
        eVotingService.addElection(election);
    }

    @GetMapping("/get/elections")
    public List<Election> getAllElections() {
        return eVotingService.getAllElections();
    }

    @PostMapping("/add/electionChoice")
    public void addElectionChoice(@RequestBody ElectionChoice electionChoice) {
        eVotingService.addElectionChoice(electionChoice);
    }

    @GetMapping("/get/electionChoices")
    public List<ElectionChoice> getAllElectionChoices() {
        return eVotingService.getAllElectionChoices();
    }

    @GetMapping("/count/{electionId}")
    public long getCountByElectionId(@PathVariable Long electionId) {
        return eVotingService.choicesByElection(electionId);
    }

    @PostMapping("/add/vote")
    public void addVote(Authentication authentication,
                        @RequestParam Long electionId,
                        @RequestParam Long electionChoiceId) {
        User user = eVotingService.findUserByName(authentication.getName());
        eVotingService.addVote(user.getId(), electionId, electionChoiceId);
    }

    @GetMapping("/get/votes")
    public List<Vote> getAllVotes() {
        return eVotingService.getAllVotes();
    }

    @GetMapping("/count/votes")
    public long getTotalVotes() {
        return eVotingService.countTotalVotes();
    }

    @GetMapping("/count/votes/{electionName}")
    public long getVotesByElection(@PathVariable String electionName) {
        return eVotingService.countVotesByElectionName(electionName);
    }

    @GetMapping("/winner/election/{electionName}")
    public ElectionChoice getElectionWinner(@PathVariable String electionName) {
        return eVotingService.findElectionWinner(electionName);
    }
}
