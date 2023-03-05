package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.database.ConfigurationData;
import com.byko.api_3d_printing.database.repository.ConfigurationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public Optional<ConfigurationData> get(){
        return Optional.of(configurationRepository.findAll().get(0));
    }

    public ConfigurationData createEmptyConfiguration(){
        ConfigurationData configurationData = new ConfigurationData();
        configurationData.setEmail("");
        configurationData.setEmailPass("");
        configurationData.setEmailEnable(false);

        return configurationRepository.save(configurationData);
    }

    public void save(ConfigurationData configurationData){
        configurationRepository.save(configurationData);
    }





}
