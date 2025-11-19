package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserPopulator {

    private final UserRepository userRepository;

    @Autowired
    public UserPopulator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create the test users used by the tasks.
     * Call this from tests (userPopulator.populate()).
     */
    public void populate() {
        // replace these initial balances with whatever the test expects
        userRepository.save(new UserRecord("waldorf", BigDecimal.valueOf(0.0)));
        userRepository.save(new UserRecord("wilbur", BigDecimal.valueOf(0.0)));
        userRepository.save(new UserRecord("other", BigDecimal.valueOf(0.0)));
    }
}
