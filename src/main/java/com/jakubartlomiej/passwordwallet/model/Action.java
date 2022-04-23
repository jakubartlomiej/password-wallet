package com.jakubartlomiej.passwordwallet.model;

import com.jakubartlomiej.passwordwallet.model.enums.ActionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private ActionType name;
    private String description;
}
