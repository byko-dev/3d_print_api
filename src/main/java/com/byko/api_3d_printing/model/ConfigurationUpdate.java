package com.byko.api_3d_printing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfigurationUpdate {

    public String email;
    public String password;
    public boolean enabled;
}
