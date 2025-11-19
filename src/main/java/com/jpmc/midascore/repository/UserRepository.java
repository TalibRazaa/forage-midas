package com.jpmc.midascore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jpmc.midascore.entity.UserRecord;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserRecord, Long> {
    Optional<UserRecord> findByAccountId(String accountId);
}
