package com.jakubartlomiej.passwordwallet.repository;

import com.jakubartlomiej.passwordwallet.model.DataChange;
import com.jakubartlomiej.passwordwallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataChangeRepository extends JpaRepository<DataChange, Long> {
    List<DataChange> findByUser(User user);
}
