package com.byko.api_3d_printing.model;

public record RecaptchaResultDTO(
        boolean success,
        String challenge_ts,
        String hostname,
        float score,
        String action) {
}
