package com.portfolio.users.service;

import com.portfolio.users.entity.UserEntity;
import com.portfolio.users.entity.UserStatus;
import com.portfolio.users.generated.model.CreateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRequest;
import com.portfolio.users.generated.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, OffsetDateTime.class, UserStatus.class, java.util.ArrayList.class, java.util.List.class, java.util.stream.Collectors.class})
public interface UserMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "status", expression = "java(UserStatus.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(OffsetDateTime.now())")
    @Mapping(target = "version", constant = "0L")
    @Mapping(target = "roles", expression = "java(normalizeRoles(request.getRoles()))")
    UserEntity toEntity(CreateUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget UserEntity entity);

    @Mapping(target = "status", expression = "java(mapStatusToDto(entity.getStatus()))")
    @Mapping(target = "roles", expression = "java(entity.getRoles() == null ? List.of() : entity.getRoles())")
    User toUser(UserEntity entity);

    default UserStatus mapStatusToEntity(com.portfolio.users.generated.model.UserStatus status) {
        return status == null ? null : UserStatus.valueOf(status.getValue());
    }

    default com.portfolio.users.generated.model.UserStatus mapStatusToDto(UserStatus status) {
        return status == null ? null : com.portfolio.users.generated.model.UserStatus.fromValue(status.name());
    }

    default java.util.List<String> normalizeRoles(java.util.List<String> roles) {
        if (roles == null) {
            return new java.util.ArrayList<>();
        }
        return roles.stream()
            .filter(role -> role != null && !role.isBlank())
            .map(String::trim)
            .distinct()
            .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    }

    @AfterMapping
    default void touchTimestamps(@MappingTarget UserEntity entity) {
        entity.setUpdatedAt(OffsetDateTime.now());
        if (entity.getStatus() != UserStatus.ACTIVE && entity.getInactiveSince() == null) {
            entity.setInactiveSince(OffsetDateTime.now());
        }
    }
}
