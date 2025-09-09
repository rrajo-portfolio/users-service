package com.rrajo.users.dto;

import jakarta.validation.constraints.*;

public record UpdateUserRequest(@Size(max = 120) String fullName, @Email @Size(max = 160) String email,
                                @Size(max = 30) String phone) {
}