package com.byko.api_3d_printing.database.repository;

import com.byko.api_3d_printing.database.ConfigurationData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends MongoRepository<ConfigurationData, String> {


}
