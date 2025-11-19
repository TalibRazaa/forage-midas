package com.jpmc.midascore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Test Kafka consumer placed under src/test/java.
 * Listens to "transactions" topic and forwards the raw CSV message
 * to TransactionProcessor.process(message). Also logs the raw message.
 */
@Component
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final TransactionProcessor processor;

    public KafkaConsumer(TransactionProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(topics = "transactions", groupId = "forage-test")
    public void listen(String message) {
        logger.info("Test KafkaConsumer received raw message: {}", message);

        try {
            processor.process(message);
        } catch (Exception e) {
            logger.error("Error forwarding test message to TransactionProcessor. message='{}'. Error={}", message, e.toString(), e);
        }
    }
}
