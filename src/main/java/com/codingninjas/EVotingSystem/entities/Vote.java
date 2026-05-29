package com.codingninjas.EVotingSystem.entities;

import jakarta.persistence.*;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private User user;

    @ManyToOne
    private Election election;

    @ManyToOne
    private ElectionChoice electionChoice;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public ElectionChoice getElectionChoice() { return electionChoice; }
    public void setElectionChoice(ElectionChoice electionChoice) { this.electionChoice = electionChoice; }
}
