package com.portfolio.users.repository;

import com.portfolio.users.entity.UserEntity;
import com.portfolio.users.entity.UserStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByEmailIgnoreCase should return stored user")
    void findByEmailIgnoreCase() {
        UserEntity entity = baseUser("dev@portfolio.test");
        userRepository.save(entity);

        Optional<UserEntity> result = userRepository.findByEmailIgnoreCase("DEV@portfolio.test");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("dev@portfolio.test");
    }

    @Test
    @DisplayName("countByStatus should return amount of users per status")
    void countByStatus() {
        userRepository.save(baseUser("active@portfolio.test"));
        UserEntity inactive = baseUser("inactive@portfolio.test");
        inactive.setStatus(UserStatus.INACTIVE);
        inactive.setInactiveSince(OffsetDateTime.now());
        userRepository.save(inactive);

        long active = userRepository.countByStatus(UserStatus.ACTIVE);
        long inactiveCount = userRepository.countByStatus(UserStatus.INACTIVE);

        assertThat(active).isEqualTo(1);
        assertThat(inactiveCount).isEqualTo(1);
    }

    private UserEntity baseUser(String email) {
        return UserEntity.builder()
            .fullName("Portfolio User")
            .email(email)
            .status(UserStatus.ACTIVE)
            .skills(List.of("Spring", "Kubernetes"))
            .createdAt(OffsetDateTime.now())
            .build();
    }
}
