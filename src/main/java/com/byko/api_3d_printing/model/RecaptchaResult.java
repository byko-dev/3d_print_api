package com.byko.api_3d_printing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecaptchaResult {

    private boolean success;
    private String challenge_ts;
    private String hostname;
    private float score;
    private String action;

}
