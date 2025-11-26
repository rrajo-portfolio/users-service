package com.portfolio.users.service;

import com.portfolio.users.entity.UserEntity;
import com.portfolio.users.entity.UserStatus;
import com.portfolio.users.generated.model.CreateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRequest;
import com.portfolio.users.generated.model.User;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-25T11:40:39+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toEntity(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.fullName( request.getFullName() );
        userEntity.email( request.getEmail() );
        userEntity.phoneNumber( request.getPhoneNumber() );
        userEntity.headline( request.getHeadline() );
        userEntity.skills( normalizeRoles( request.getSkills() ) );

        userEntity.id( UUID.randomUUID() );
        userEntity.status( UserStatus.ACTIVE );
        userEntity.createdAt( OffsetDateTime.now() );
        userEntity.version( (long) 0L );
        userEntity.roles( normalizeRoles(request.getRoles()) );

        return userEntity.build();
    }

    @Override
    public void updateEntity(UpdateUserRequest request, UserEntity entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getFullName() != null ) {
            entity.setFullName( request.getFullName() );
        }
        if ( request.getEmail() != null ) {
            entity.setEmail( request.getEmail() );
        }
        if ( request.getPhoneNumber() != null ) {
            entity.setPhoneNumber( request.getPhoneNumber() );
        }
        if ( request.getHeadline() != null ) {
            entity.setHeadline( request.getHeadline() );
        }
        if ( entity.getSkills() != null ) {
            List<String> list = normalizeRoles( request.getSkills() );
            if ( list != null ) {
                entity.getSkills().clear();
                entity.getSkills().addAll( list );
            }
        }
        else {
            List<String> list = normalizeRoles( request.getSkills() );
            if ( list != null ) {
                entity.setSkills( list );
            }
        }

        touchTimestamps( entity );
    }

    @Override
    public User toUser(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        User user = new User();

        user.setId( entity.getId() );
        user.setFullName( entity.getFullName() );
        user.setEmail( entity.getEmail() );
        user.setPhoneNumber( entity.getPhoneNumber() );
        user.setHeadline( entity.getHeadline() );
        user.setSkills( normalizeRoles( entity.getSkills() ) );
        user.setCreatedAt( entity.getCreatedAt() );
        user.setUpdatedAt( entity.getUpdatedAt() );

        user.setStatus( mapStatusToDto(entity.getStatus()) );
        user.setRoles( entity.getRoles() == null ? List.of() : entity.getRoles() );

        return user;
    }
}
