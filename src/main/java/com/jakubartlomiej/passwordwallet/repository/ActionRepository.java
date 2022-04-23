package com.jakubartlomiej.passwordwallet.repository;

import com.jakubartlomiej.passwordwallet.model.Action;
import com.jakubartlomiej.passwordwallet.model.enums.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    Optional<Action> findByName(ActionType name);
}
