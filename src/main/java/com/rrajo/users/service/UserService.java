package com.rrajo.users.service;

import com.rrajo.users.domain.User;
import com.rrajo.users.dto.*;
import com.rrajo.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public UserResponse create(CreateUserRequest req) {
        if (repo.existsByEmail(req.email())) throw new IllegalArgumentException("Email already in use");
        User u = new User();
        u.setFullName(req.fullName());
        u.setEmail(req.email());
        u.setPhone(req.phone());
        return map(repo.save(u));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return repo.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return repo.findById(id).map(this::map).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req) {
        User u = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (req.fullName() != null) u.setFullName(req.fullName());
        if (req.email() != null) {
            if (repo.existsByEmail(req.email()) && !req.email().equalsIgnoreCase(u.getEmail()))
                throw new IllegalArgumentException("Email already in use");
            u.setEmail(req.email());
        }
        if (req.phone() != null) u.setPhone(req.phone());
        return map(repo.save(u));
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private UserResponse map(User u) {
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getCreatedAt());
    }
}