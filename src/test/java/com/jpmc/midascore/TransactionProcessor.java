package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class TransactionProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);
    private final DatabaseConduit databaseConduit;

    public TransactionProcessor(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @Transactional
    public void process(String line) {

        if (line == null || line.isBlank()) return;

        String[] parts = line.split(",");
        if (parts.length < 3) {
            logger.warn("Bad line: {}", line);
            return;
        }

        String from = parts[0].trim();
        String to = parts[1].trim();
        BigDecimal amount = new BigDecimal(parts[2].trim());

        Optional<UserRecord> senderOpt = databaseConduit.findByAccountId(from);
        Optional<UserRecord> receiverOpt = databaseConduit.findByAccountId(to);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            logger.warn("User not found for line: {}", line);
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord receiver = receiverOpt.get();

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        databaseConduit.save(sender);
        databaseConduit.save(receiver);

        logger.info("Processed txn: {} → {} : {}", from, to, amount);
    }
}
