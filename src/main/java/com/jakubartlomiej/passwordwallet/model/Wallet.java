package com.jakubartlomiej.passwordwallet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String webAddress;
    private String description;
    private String login;
    private String password;
    @OneToOne
    private User user;
}
