package com.portfolio.users.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.users.entity.UserEntity;
import com.portfolio.users.entity.UserStatus;
import com.portfolio.users.generated.model.CreateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRequest;
import com.portfolio.users.generated.model.User;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void toEntityNormalizesRolesAndDefaults() {
        CreateUserRequest request = new CreateUserRequest()
            .fullName("Portfolio Engineer")
            .email("portfolio@test")
            .roles(List.of(" catalog_read", "catalog_read", "users_write", "  "))
            .headline("Cloud ready");

        UserEntity entity = mapper.toEntity(request);

        assertThat(entity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(entity.getRoles()).containsExactly("catalog_read", "users_write");
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getVersion()).isZero();
    }

    @Test
    void updateEntityIgnoresStatusAndUpdatesTimestamps() {
        OffsetDateTime previousUpdate = OffsetDateTime.now().minusDays(2);
        UserEntity entity = UserEntity.builder()
            .id(UUID.randomUUID())
            .fullName("Legacy User")
            .email("legacy@test")
            .status(UserStatus.INACTIVE)
            .createdAt(OffsetDateTime.now().minusMonths(1))
            .updatedAt(previousUpdate)
            .build();

        UpdateUserRequest request = new UpdateUserRequest()
            .fullName("Modern User")
            .headline("Engineer")
            .status(com.portfolio.users.generated.model.UserStatus.ACTIVE);

        mapper.updateEntity(request, entity);

        assertThat(entity.getFullName()).isEqualTo("Modern User");
        assertThat(entity.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(entity.getUpdatedAt()).isAfter(previousUpdate);
        assertThat(entity.getInactiveSince()).isNotNull();
    }

    @Test
    void toUserMapsStatusAndRoles() {
        UserEntity entity = UserEntity.builder()
            .id(UUID.randomUUID())
            .fullName("Mapper Test")
            .email("mapper@test")
            .status(UserStatus.ACTIVE)
            .roles(List.of("catalog_read"))
            .createdAt(OffsetDateTime.now())
            .build();

        User dto = mapper.toUser(entity);

        assertThat(dto.getStatus()).isEqualTo(com.portfolio.users.generated.model.UserStatus.ACTIVE);
        assertThat(dto.getRoles()).containsExactly("catalog_read");
    }
}
