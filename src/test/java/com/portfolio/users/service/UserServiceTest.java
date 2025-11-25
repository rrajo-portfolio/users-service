package com.portfolio.users.service;

import com.portfolio.users.entity.UserEntity;
import com.portfolio.users.entity.UserStatus;
import com.portfolio.users.exception.ConflictException;
import com.portfolio.users.exception.ResourceNotFoundException;
import com.portfolio.users.generated.model.CreateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRolesRequest;
import com.portfolio.users.generated.model.User;
import com.portfolio.users.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserMapper userMapper;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    @DisplayName("createUser should persist entity and return DTO")
    void createUserPersistsEntity() {
        CreateUserRequest request = new CreateUserRequest()
            .fullName("Portfolio Candidate")
            .email("candidate@portfolio.test")
            .skills(List.of("Java", "Docker"));

        when(userRepository.findByEmailIgnoreCase("candidate@portfolio.test")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(request);

        assertThat(result.getFullName()).isEqualTo("Portfolio Candidate");
        assertThat(result.getEmail()).isEqualTo("candidate@portfolio.test");
        assertThat(result.getStatus()).isEqualTo(com.portfolio.users.generated.model.UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("createUser should throw when email already exists")
    void createUserDuplicateEmailThrows() {
        CreateUserRequest request = new CreateUserRequest().email("duplicate@test");
        when(userRepository.findByEmailIgnoreCase("duplicate@test")).thenReturn(Optional.of(new UserEntity()));

        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("updateUser should apply status transitions and save entity")
    void updateUserAppliesStatusChange() {
        UUID id = UUID.randomUUID();
        UserEntity entity = UserEntity.builder()
            .id(id)
            .fullName("Portfolio User")
            .email("portfolio@test")
            .status(UserStatus.ACTIVE)
            .createdAt(OffsetDateTime.now())
            .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userRepository.save(entity)).thenReturn(entity);

        UpdateUserRequest request = new UpdateUserRequest()
            .status(com.portfolio.users.generated.model.UserStatus.INACTIVE)
            .headline("Inactive for sabbatical");

        User result = userService.updateUser(id, request);

        assertThat(entity.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(result.getStatus()).isEqualTo(com.portfolio.users.generated.model.UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("getUser should throw when id is missing")
    void getUserNotFoundThrows() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateUserRoles should normalize and persist roles")
    void updateUserRolesPersistsRoles() {
        UUID id = UUID.randomUUID();
        UserEntity entity = UserEntity.builder()
            .id(id)
            .fullName("Role User")
            .email("roles@test")
            .status(UserStatus.ACTIVE)
            .createdAt(OffsetDateTime.now())
            .roles(new java.util.ArrayList<>(List.of("legacy")))
            .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userRepository.save(entity)).thenReturn(entity);

        UpdateUserRolesRequest request = new UpdateUserRolesRequest()
            .roles(List.of("catalog_read", "users_write", "catalog_read", "  "));

        User result = userService.updateUserRoles(id, request);

        assertThat(entity.getRoles()).containsExactly("catalog_read", "users_write");
        assertThat(result.getRoles()).containsExactly("catalog_read", "users_write");
    }
}
