package com.portfolio.users.service;

import com.portfolio.users.entity.UserEntity;
import com.portfolio.users.entity.UserStatus;
import com.portfolio.users.exception.ConflictException;
import com.portfolio.users.exception.ResourceNotFoundException;
import com.portfolio.users.generated.model.CreateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRequest;
import com.portfolio.users.generated.model.UpdateUserRolesRequest;
import com.portfolio.users.generated.model.User;
import com.portfolio.users.generated.model.UserPage;
import com.portfolio.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public UserPage listUsers(Integer page, Integer size, String status) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size,
            Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<UserEntity> result;
        if (status != null) {
            UserStatus desired = UserStatus.valueOf(status);
            result = repository.findAll((root, query, cb) -> cb.equal(root.get("status"), desired), pageable);
        } else {
            result = repository.findAll(pageable);
        }

        return new UserPage()
            .content(result.stream().map(mapper::toUser).toList())
            .page(result.getNumber())
            .size(result.getSize())
            .totalElements(result.getTotalElements())
            .totalPages(result.getTotalPages());
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        Optional<UserEntity> existing = repository.findByEmailIgnoreCase(request.getEmail());
        if (existing.isPresent()) {
            return mapper.toUser(existing.get());
        }
        UserEntity entity = mapper.toEntity(request);
        return mapper.toUser(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public User getUser(UUID id) {
        return mapper.toUser(findById(id));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        UserEntity entity = repository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResourceNotFoundException("User with email %s not found".formatted(email)));
        return mapper.toUser(entity);
    }

    @Transactional
    public User updateUser(UUID id, UpdateUserRequest request) {
        UserEntity entity = findById(id);
        if (request.getEmail() != null) {
            Optional<UserEntity> existing = repository.findByEmailIgnoreCase(request.getEmail());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new ConflictException("Email already registered: " + request.getEmail());
            }
        }
        mapper.updateEntity(request, entity);
        if (request.getStatus() != null) {
            entity.setStatus(mapper.mapStatusToEntity(request.getStatus()));
        }
        return mapper.toUser(repository.save(entity));
    }

    @Transactional
    public User updateUserRoles(UUID id, UpdateUserRolesRequest request) {
        UserEntity entity = findById(id);
        List<String> normalized = request.getRoles() == null
            ? List.of()
            : request.getRoles().stream()
                .filter(role -> role != null && !role.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        entity.setRoles(normalized);
        return mapper.toUser(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        UserEntity entity = findById(id);
        entity.setStatus(UserStatus.ARCHIVED);
        repository.save(entity);
    }

    @Transactional(readOnly = true)
    public boolean exists(UUID id) {
        return repository.existsById(id);
    }

    private UserEntity findById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User %s not found".formatted(id)));
    }
}
