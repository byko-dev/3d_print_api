package com.byko.api_3d_printing.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
public record ChangeStatusDTO(
        @NotNull @NotEmpty
        String projectId,
        @Size(min = 0, max = 4, message = "Project status miss requirements of value between 0-4")
        Integer newStatus) {
}

