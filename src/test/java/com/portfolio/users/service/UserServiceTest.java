package com.portfolio.users.service;

import com.portfolio.users.entity.UserEntity;
import com.portfolio.users.entity.UserStatus;
import com.portfolio.users.exception.ResourceNotFoundException;
import com.portfolio.users.generated.model.CreateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRolesRequest;
import com.portfolio.users.generated.model.User;
import com.portfolio.users.generated.model.UserPage;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    @DisplayName("createUser should return existing user when email already exists")
    void createUserDuplicateEmailReturnsExisting() {
        CreateUserRequest request = new CreateUserRequest()
            .fullName("Existing User")
            .email("duplicate@test");
        UserEntity existing = UserEntity.builder()
            .id(UUID.randomUUID())
            .fullName("Existing User")
            .email("duplicate@test")
            .status(UserStatus.ACTIVE)
            .createdAt(OffsetDateTime.now())
            .build();
        when(userRepository.findByEmailIgnoreCase("duplicate@test")).thenReturn(Optional.of(existing));

        User result = userService.createUser(request);

        assertThat(result.getEmail()).isEqualTo("duplicate@test");
        verify(userRepository, never()).save(any(UserEntity.class));
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

    @Test
    @DisplayName("listUsers without status uses default pagination and maps content")
    void listUsersDefaultsPagination() {
        UserEntity entity = sampleEntity(UserStatus.ACTIVE);
        Page<UserEntity> page = new PageImpl<>(
            List.of(entity),
            PageRequest.of(0, 20),
            1
        );
        when(userRepository.findAll(org.mockito.ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        UserPage result = userService.listUsers(null, null, null);

        assertThat(result.getContent()).hasSize(1);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAll(pageableCaptor.capture());
        Pageable usedPageable = pageableCaptor.getValue();
        assertThat(usedPageable.getPageNumber()).isZero();
        assertThat(usedPageable.getPageSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("listUsers with status applies specification filter")
    void listUsersAppliesStatusFilter() {
        UserEntity entity = sampleEntity(UserStatus.INACTIVE);
        Page<UserEntity> page = new PageImpl<>(List.of(entity));
        when(userRepository.findAll(org.mockito.ArgumentMatchers.<Specification<UserEntity>>any(), org.mockito.ArgumentMatchers.any(Pageable.class)))
            .thenReturn(page);

        UserPage result = userService.listUsers(1, 5, "INACTIVE");

        assertThat(result.getContent()).hasSize(1);
        verify(userRepository).findAll(org.mockito.ArgumentMatchers.<Specification<UserEntity>>any(), org.mockito.ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @DisplayName("getUserByEmail should return mapped DTO")
    void getUserByEmailReturnsDto() {
        UserEntity entity = sampleEntity(UserStatus.ACTIVE);
        when(userRepository.findByEmailIgnoreCase("portfolio@test")).thenReturn(Optional.of(entity));

        User result = userService.getUserByEmail("portfolio@test");

        assertThat(result.getEmail()).isEqualTo("portfolio@test");
    }

    @Test
    @DisplayName("delete should mark user as archived")
    void deleteMarksUserArchived() {
        UserEntity entity = sampleEntity(UserStatus.ACTIVE);
        when(userRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(userRepository.save(entity)).thenReturn(entity);

        userService.delete(entity.getId());

        assertThat(entity.getStatus()).isEqualTo(UserStatus.ARCHIVED);
        verify(userRepository).save(entity);
    }

    @Test
    @DisplayName("exists delegates to repository")
    void existsDelegatesToRepository() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);

        assertThat(userService.exists(id)).isTrue();
        verify(userRepository).existsById(id);
    }

    private UserEntity sampleEntity(UserStatus status) {
        return UserEntity.builder()
            .id(UUID.randomUUID())
            .fullName("Tester")
            .email("portfolio@test")
            .status(status)
            .roles(new java.util.ArrayList<>(List.of("catalog_read")))
            .createdAt(OffsetDateTime.now())
            .build();
    }
}
