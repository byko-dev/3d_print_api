package com.byko.api_3d_printing.database.repository;

import com.byko.api_3d_printing.database.ImageDAO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImagesRepository extends MongoRepository<ImageDAO, String> {

    Optional<ImageDAO> findById(String id);

}
