package com.academicevents.dto;

import com.academicevents.model.UserRole;

public record AuthResponse (
    Long id,
    String firstName,
    String lastName,
    String email,
    UserRole role
) {
}
