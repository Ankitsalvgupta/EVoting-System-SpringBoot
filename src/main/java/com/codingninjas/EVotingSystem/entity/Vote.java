package com.codingninjas.EVotingSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vote",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_vote_user_election",
                columnNames = {"user_id", "election_id"}))
@Getter
@Setter
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    @ManyToOne
    @JoinColumn(name = "election_choice_id")
    private ElectionChoice electionChoice;
}