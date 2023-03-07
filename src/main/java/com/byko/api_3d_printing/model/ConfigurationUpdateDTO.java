package com.byko.api_3d_printing.model;

public record ConfigurationUpdateDTO(
        String email,
        String password,
        boolean enabled) {
}
