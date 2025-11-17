package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class TransactionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

    private final UserRepository userRepository;

    public TransactionProcessor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Accepts transaction lines in either:
     *  - "accountId,amount"                -> credit that account by amount (single-party credit)
     *  - "fromAccountId,toAccountId,amount"-> move amount from 'from' to 'to'
     *
     * The method is transactional so DB updates are atomic per message.
     */
    @Transactional
    public void process(String line) {
        if (line == null) {
            logger.warn("Received null transaction line");
            return;
        }

        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            logger.debug("Ignoring empty transaction line");
            return;
        }

        String[] parts = trimmed.split(",");
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();

        try {
            if (parts.length == 2) {
                // format: accountId, amount -> credit account
                String accountId = parts[0];
                BigDecimal amount = new BigDecimal(parts[1]);
                creditAccount(accountId, amount);
                logger.info("Processed credit: {} -> +{}", accountId, amount);
            } else if (parts.length == 3) {
                // format: fromAccountId, toAccountId, amount -> debit from, credit to
                String from = parts[0];
                String to = parts[1];
                BigDecimal amount = new BigDecimal(parts[2]);

                // debit from
                boolean debited = debitAccount(from, amount);
                if (debited) {
                    creditAccount(to, amount);
                    logger.info("Processed transfer: {} -> {} amount {}", from, to, amount);
                } else {
                    logger.warn("Skipping transfer because debit failed for account '{}' amount {}", from, amount);
                }
            } else {
                logger.warn("Unrecognized transaction format (expected 2 or 3 comma-separated fields): {}", line);
            }
        } catch (NumberFormatException nfe) {
            logger.error("Failed to parse amount in transaction line: {} ({})", line, nfe.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error processing transaction line '{}': {}", line, ex.getMessage(), ex);
            // Let exception bubble? We caught it and logged to avoid stopping the listener.
        }
    }

    private boolean debitAccount(String accountId, BigDecimal amount) {
        Optional<UserRecord> opt = userRepository.findByAccountId(accountId);
        if (opt.isEmpty()) {
            logger.error("Debit failed: account '{}' not found", accountId);
            return false;
        }
        UserRecord u = opt.get();
        BigDecimal old = u.getBalance();
        if (old == null) old = BigDecimal.ZERO;
        if (old.compareTo(amount) < 0) {
            logger.error("Debit failed: insufficient funds for '{}'. balance={}, amount={}", accountId, old, amount);
            return false;
        }
        u.setBalance(old.subtract(amount));
        userRepository.save(u);
        logger.debug("Debited {} from {} ({} -> {})", amount, accountId, old, u.getBalance());
        return true;
    }

    private void creditAccount(String accountId, BigDecimal amount) {
        Optional<UserRecord> opt = userRepository.findByAccountId(accountId);
        UserRecord u;
        if (opt.isPresent()) {
            u = opt.get();
            BigDecimal old = u.getBalance();
            if (old == null) old = BigDecimal.ZERO;
            u.setBalance(old.add(amount));
        } else {
            // create new account record if not found (defensive; adjust if you don't want auto-creation)
            u = new UserRecord();
            u.setAccountId(accountId);
            u.setBalance(amount);
            logger.info("Created account '{}' with initial balance {}", accountId, amount);
        }
        userRepository.save(u);
        logger.debug("Credited {} to {} (new balance {})", amount, accountId, u.getBalance());
    }
}
