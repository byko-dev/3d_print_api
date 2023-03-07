package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.database.ConfigurationDAO;
import com.byko.api_3d_printing.database.repository.ConfigurationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public Optional<ConfigurationDAO> get(){
        return Optional.of(configurationRepository.findAll().get(0));
    }

    public ConfigurationDAO createEmptyConfiguration(){
        ConfigurationDAO configurationData = new ConfigurationDAO();
        configurationData.setEmail("");
        configurationData.setEmailPass("");
        configurationData.setEmailEnable(false);

        return configurationRepository.save(configurationData);
    }

    public void save(ConfigurationDAO configurationData){
        configurationRepository.save(configurationData);
    }





}
