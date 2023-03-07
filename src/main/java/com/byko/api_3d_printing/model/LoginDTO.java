package com.byko.api_3d_printing.model;

public record LoginDTO(
        String username,
        String password,
        String captchaResponse) {

}
