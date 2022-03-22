package com.jakubartlomiej.passwordwallet.repository;

import com.jakubartlomiej.passwordwallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUserId(long id);
}
