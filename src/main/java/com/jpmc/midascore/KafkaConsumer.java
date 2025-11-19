package com.jpmc.midascore;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Simple Kafka listener. Adjust topic/groupId via annotations or application.yml if needed.
 */
@Component
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final TransactionProcessor processor;

    public KafkaConsumer(TransactionProcessor processor) {
        this.processor = processor;
    }

    // Listen to 'transactions' topic; groupId chosen so multiple instances can cooperate.
    @KafkaListener(topics = "transactions", groupId = "forage-app-group")
    public void listen(ConsumerRecord<String, String> record) {
        String message = record.value();
        logger.debug("KafkaConsumer received message: partition={}, offset={}, value={}",
                record.partition(), record.offset(), message);
        processor.process(message);
    }
}
