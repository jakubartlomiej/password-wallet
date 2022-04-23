package com.jakubartlomiej.passwordwallet.service;

import com.jakubartlomiej.passwordwallet.exception.ActionNotFoundException;
import com.jakubartlomiej.passwordwallet.model.Action;
import com.jakubartlomiej.passwordwallet.model.enums.ActionType;
import com.jakubartlomiej.passwordwallet.repository.ActionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ActionService {
    private final ActionRepository actionRepository;

    public long count() {
        return actionRepository.count();
    }

    public void save(Action action) {
        actionRepository.save(action);
    }

    public Action findByName(ActionType name) {
        return actionRepository.findByName(name)
                .orElseThrow(() -> new ActionNotFoundException("Not found action: " + name));
    }
}
