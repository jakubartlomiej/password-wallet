package com.jakubartlomiej.passwordwallet.service;

import com.jakubartlomiej.passwordwallet.model.FunctionRun;
import com.jakubartlomiej.passwordwallet.repository.FunctionRunRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FunctionRunService {
    private final FunctionRunRepository functionRunRepository;

    public void save(FunctionRun functionRun){
        functionRunRepository.save(functionRun);
    }

    public List<FunctionRun> findByUserId(long userId){
        return functionRunRepository.findByUserId(userId);
    }
}
