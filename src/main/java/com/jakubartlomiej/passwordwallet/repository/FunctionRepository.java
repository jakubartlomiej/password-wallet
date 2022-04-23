package com.jakubartlomiej.passwordwallet.repository;

import com.jakubartlomiej.passwordwallet.model.Function;
import com.jakubartlomiej.passwordwallet.model.enums.FunctionName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Long> {
    Optional<Function> findByName(FunctionName name);
}
