package com.byko.api_3d_printing.utils;

import com.byko.api_3d_printing.model.RecaptchaResultDTO;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@NoArgsConstructor
public class CaptchaValidation {

    @Value("${captcha.secrect.key}")
    private String secretKey;

    public boolean isValid(String captchaResponse){
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + secretKey + "&response=" + captchaResponse;

        RestTemplate restTemplate = new RestTemplate();
        RecaptchaResultDTO result = restTemplate.getForObject(url, RecaptchaResultDTO.class);

        //TODO later remove this statement, only development purpose
        System.out.println("Captcha results " + result.score() + " " + result.challenge_ts());

        if(result.success()){
            if(0.5 <= result.score()) return true;
            else return false;
        }
        return false;
    }
}
