package com.byko.api_3d_printing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangeStatusRequest {

    @NotNull
    @NotEmpty
    private String projectId;

    @Size(min = 0, max = 4, message = "Project status miss requirements of value between 0-4")
    private Integer newStatus;
}

