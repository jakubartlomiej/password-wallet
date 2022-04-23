package com.jakubartlomiej.passwordwallet.service;

import com.jakubartlomiej.passwordwallet.exception.DataChangeNotFoundException;
import com.jakubartlomiej.passwordwallet.model.DataChange;
import com.jakubartlomiej.passwordwallet.model.User;
import com.jakubartlomiej.passwordwallet.repository.DataChangeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DataChangeService {
    private final DataChangeRepository dataChangeRepository;

    public void save(DataChange dataChange) {
        dataChangeRepository.save(dataChange);
    }

    public List<DataChange> findByUser(User user) {
        return dataChangeRepository.findByUser(user);
    }

    public DataChange findById(long id) {
        return dataChangeRepository.findById(id)
                .orElseThrow(() -> new DataChangeNotFoundException("Not found data change: " + id));
    }
}
