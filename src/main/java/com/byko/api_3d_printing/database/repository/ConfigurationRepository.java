package com.byko.api_3d_printing.database.repository;

import com.byko.api_3d_printing.database.ConfigurationDAO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigurationRepository extends MongoRepository<ConfigurationDAO, String> {

    List<ConfigurationDAO> findAll();

}
