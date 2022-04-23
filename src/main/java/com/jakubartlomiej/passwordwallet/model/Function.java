package com.jakubartlomiej.passwordwallet.model;

import com.jakubartlomiej.passwordwallet.model.enums.FunctionName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private FunctionName name;
    private String description;
}
