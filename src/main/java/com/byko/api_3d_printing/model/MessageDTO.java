package com.byko.api_3d_printing.model;

import com.byko.api_3d_printing.database.enums.User;

public record MessageDTO(
        String id,
        String fileId,
        String description,
        User userType,
        String data,
        String fileName,
        String username) {
}
