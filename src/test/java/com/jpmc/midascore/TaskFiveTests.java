package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.jpmc.midascore.repository.UserRepository;

import java.util.Optional;
import java.time.Duration;
import java.time.Instant;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1)
public class TaskFiveTests {

    static final Logger logger = LoggerFactory.getLogger(TaskFiveTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRepository userRepository;

    // Ensure Spring Kafka clients use the embedded broker started for tests.
    @DynamicPropertySource
    static void kafkaProps(DynamicPropertyRegistry registry) {
        // EmbeddedKafka exposes the broker list through the system property
        // spring.embedded.kafka.brokers — Spring's testkit will set that when the broker starts.
        registry.add("spring.kafka.bootstrap-servers",
                     () -> System.getProperty("spring.embedded.kafka.brokers"));
    }

    @Test
    void task_five_verifier() throws InterruptedException {
        logger.info("=== TASK 5 TEST START ===");

        // 1) Populate test users
        userPopulator.populate();

        // 2) Load test transaction data (replace with the path your tests expect)
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");

        // 3) Send all transactions to Kafka
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // 4) Poll for Wilbur to appear and for his balance to settle
        Instant deadline = Instant.now().plus(Duration.ofSeconds(20));
        Optional<?> wilburOpt = Optional.empty();

        while (Instant.now().isBefore(deadline)) {
            try {
                // repository method name may vary; keep generic and let compile-time error show if yours differs
                wilburOpt = userRepository.findByAccountId("wilbur");
            } catch (Exception e) {
                logger.warn("Repository lookup failed (may be starting): {}", e.getMessage());
            }

            if (wilburOpt != null && wilburOpt.isPresent()) {
                Object wilbur = wilburOpt.get();

                try {
                    // attempt to call getBalance() reflectively if type differs
                    java.lang.reflect.Method m = wilbur.getClass().getMethod("getBalance");
                    Object balance = m.invoke(wilbur);
                    logger.info("WILBUR FINAL BALANCE = {}", balance);
                } catch (NoSuchMethodException nsme) {
                    logger.info("WILBUR record found but no getBalance() method on class {}", wilbur.getClass().getName());
                } catch (Exception ex) {
                    logger.warn("Error reading Wilbur balance via reflection: {}", ex.getMessage());
                }

                logger.info("Task 5 verification succeeded (Wilbur found).");
                return; // test passes
            }

            Thread.sleep(500); // short pause then re-check
        }

        // If we reach here, Wilbur wasn't found within timeout
        logger.error("Wilbur was NOT found in the database after waiting. Task 5 failed.");
        // Let the test fail explicitly
        org.junit.jupiter.api.Assertions.fail("Wilbur not found in DB after publishing transactions");
    }
}
