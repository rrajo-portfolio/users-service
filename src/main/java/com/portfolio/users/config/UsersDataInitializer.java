package com.portfolio.users.config;

import com.portfolio.users.entity.UserEntity;
import com.portfolio.users.entity.UserStatus;
import com.portfolio.users.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersDataInitializer {

    private final UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void preloadUsers() {
        if (userRepository.count() > 0) {
            log.debug("Users already present, skipping demo data seeding");
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        List<UserEntity> demoUsers = List.of(
            UserEntity.builder()
                .id(UUID.fromString("9ea8e86a-105e-460a-bd79-a983bf4d9e84"))
                .fullName("Lucía Prieto")
                .email("lucia.prieto@portfolio.dev")
                .phoneNumber("+34910001122")
                .headline("Portafolio Admin & DevOps Lead")
                .status(UserStatus.ACTIVE)
                .skills(List.of("Spring Boot", "Angular", "Kubernetes"))
                .roles(List.of("portfolio_admin", "catalog_admin", "users_admin"))
                .createdAt(now.minusMonths(6))
                .updatedAt(now.minusDays(3))
                .build(),
            UserEntity.builder()
                .id(UUID.fromString("b13b2039-91ff-4b27-a479-f2e59c8794d8"))
                .fullName("Marco Sánchez")
                .email("marco.sanchez@portfolio.dev")
                .phoneNumber("+34910004567")
                .headline("Observability & Platform Engineer")
                .status(UserStatus.ACTIVE)
                .skills(List.of("Grafana", "Prometheus", "Keycloak"))
                .roles(List.of("orders_read", "orders_write"))
                .createdAt(now.minusMonths(4))
                .updatedAt(now.minusDays(10))
                .build(),
            UserEntity.builder()
                .id(UUID.fromString("3f85b260-7182-11e8-9c2d-fa7ae01bbebc"))
                .fullName("Daniela Romero")
                .email("daniela.romero@portfolio.dev")
                .phoneNumber("+34910009988")
                .headline("Support & Compliance Specialist")
                .status(UserStatus.INACTIVE)
                .skills(List.of("Auditoría", "Keycloak", "Soporte Nivel 2"))
                .roles(List.of("catalog_read"))
                .createdAt(now.minusMonths(8))
                .updatedAt(now.minusMonths(1))
                .inactiveSince(now.minusMonths(1))
                .build()
        );

        userRepository.saveAll(demoUsers);
        log.info("Seeded {} demo users for governance showcase", demoUsers.size());
    }
}
