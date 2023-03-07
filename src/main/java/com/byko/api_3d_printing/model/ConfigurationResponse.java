package com.byko.api_3d_printing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfigurationResponse {

    public String email;
    public boolean enabled;
}
