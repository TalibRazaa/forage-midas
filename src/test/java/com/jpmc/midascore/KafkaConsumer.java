package com.jpmc.midascore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final TransactionProcessor processor;

    public KafkaConsumer(TransactionProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(topics = "transactions", groupId = "forage-test")
    public void listen(String message) {
        logger.info("KafkaConsumer received: {}", message);
        processor.process(message);
    }
}
