package com.jakubartlomiej.passwordwallet.service;

import com.jakubartlomiej.passwordwallet.exception.FunctionNotFoundException;
import com.jakubartlomiej.passwordwallet.model.Function;
import com.jakubartlomiej.passwordwallet.model.enums.FunctionName;
import com.jakubartlomiej.passwordwallet.repository.FunctionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FunctionService {
    private final FunctionRepository functionRepository;

    public void save(Function function) {
        functionRepository.save(function);
    }

    public long count() {
        return functionRepository.count();
    }

    public Function findByName(FunctionName name) {
        return functionRepository.findByName(name)
                .orElseThrow(() -> new FunctionNotFoundException("Not found function :" + name));
    }
}
