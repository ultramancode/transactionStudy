package com.example.transactiontest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    public Member(String username) {
        this.username = username;
    }
}