package com.jpmc.midascore.component;

import java.util.Objects;
import java.util.Optional;

import com.jpmc.midascore.entity.UserRecord;      // <- your entity package
import com.jpmc.midascore.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DatabaseConduit component: returns Optional<UserRecord> from findByAccountId
 * to match call sites that expect Optional<UserRecord>.
 */
@Component
public class DatabaseConduit {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConduit.class);

    private final UserRepository userRepository;

    public DatabaseConduit(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
    }

    @Transactional
    public UserRecord save(UserRecord userRecord) {
        Objects.requireNonNull(userRecord, "userRecord must not be null");
        UserRecord saved = Objects.requireNonNull(userRepository.save(userRecord),
                "userRepository.save returned null for: " + userRecord);
        logger.debug("Saved UserRecord id={}", saved.getId());
        return saved;
    }

    /**
     * Return Optional<UserRecord> so callers can handle absence explicitly.
     */
    public Optional<UserRecord> findByAccountId(String accountId) {
        Objects.requireNonNull(accountId, "accountId must not be null");
        return userRepository.findByAccountId(accountId);
    }
}
