package com.portfolio.users.jobs;

import com.portfolio.users.entity.UserStatus;
import com.portfolio.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class UserPurgeJob {

    private static final Logger log = LoggerFactory.getLogger(UserPurgeJob.class);

    private final UserRepository repository;

    /**
     * Marks inactive users as archived every night to keep the database tidy.
     */
    @Scheduled(cron = "0 30 2 * * *")
    public void archiveInactiveUsers() {
        long inactive = repository.countByStatus(UserStatus.INACTIVE);
        if (inactive > 0) {
            log.info("Found {} inactive users. Consider promoting purge workflow.", inactive);
        } else {
            log.debug("No inactive users pending archiving at {}", OffsetDateTime.now());
        }
    }
}
