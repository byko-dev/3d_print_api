package com.byko.api_3d_printing.utils;

import com.byko.api_3d_printing.model.RecaptchaResult;
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
        RecaptchaResult result = restTemplate.getForObject(url, RecaptchaResult.class);

        //TODO later remove this statement, only development purpose
        System.out.println("Captcha results " + result.getScore() + " " + result.getChallenge_ts());

        if(result.isSuccess()){
            if(0.5 <= result.getScore()) return true;
            else return false;
        }
        return false;
    }




}
