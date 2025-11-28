package com.portfolio.users.jobs;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.users.entity.UserStatus;
import com.portfolio.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPurgeJobTest {

    @Mock
    private UserRepository repository;

    @Test
    void archiveInactiveUsersLogsMetrics() {
        UserPurgeJob job = new UserPurgeJob(repository);
        when(repository.countByStatus(UserStatus.INACTIVE)).thenReturn(5L);

        job.archiveInactiveUsers();

        verify(repository, times(1)).countByStatus(UserStatus.INACTIVE);
    }

    @Test
    void archiveInactiveUsersHandlesEmptyResult() {
        UserPurgeJob job = new UserPurgeJob(repository);
        when(repository.countByStatus(UserStatus.INACTIVE)).thenReturn(0L);

        job.archiveInactiveUsers();

        verify(repository).countByStatus(UserStatus.INACTIVE);
    }
}
