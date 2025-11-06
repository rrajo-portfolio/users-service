package com.portfolio.users.controller;

import com.portfolio.users.generated.api.UsersApi;
import com.portfolio.users.generated.model.CreateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRequest;
import com.portfolio.users.generated.model.User;
import com.portfolio.users.generated.model.UserExists200Response;
import com.portfolio.users.generated.model.UserPage;
import com.portfolio.users.generated.model.UserStatus;
import com.portfolio.users.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

    private final UserService userService;

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_users.write','ROLE_users_write','ROLE_users-admin','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<User> createUser(CreateUserRequest createUserRequest) {
        return ResponseEntity.status(201).body(userService.createUser(createUserRequest));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_users.read','ROLE_users_read','ROLE_users-admin','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<UserPage> listUsers(Integer page, Integer size, UserStatus status) {
        return ResponseEntity.ok(userService.listUsers(page, size, status != null ? status.getValue() : null));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_users.read','ROLE_users_read','ROLE_users-admin','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<User> getUser(UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_users.read','ROLE_users_read','ROLE_users-admin','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<User> getUserByEmail(String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_users.write','ROLE_users_write','ROLE_users-admin','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<User> updateUser(UUID id, UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserRequest));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_users.write','ROLE_users_write','ROLE_users-admin','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<Void> deleteUser(UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_users.read','ROLE_users_read','ROLE_users-admin','ROLE_catalog_admin','ROLE_portfolio_admin')")
    public ResponseEntity<UserExists200Response> userExists(UUID id) {
        boolean exists = userService.exists(id);
        return ResponseEntity.ok(new UserExists200Response().exists(exists));
    }
}
