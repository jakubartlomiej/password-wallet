package com.jakubartlomiej.passwordwallet.repository;

import com.jakubartlomiej.passwordwallet.model.FunctionRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionRunRepository extends JpaRepository<FunctionRun, Long> {
    List<FunctionRun> findByUserId(long id);
}
