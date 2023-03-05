package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.database.ConfigurationData;
import com.byko.api_3d_printing.database.repository.ConfigurationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public ConfigurationData get(){
        return configurationRepository.findAll().get(0);
    }



}
