package com.byko.api_3d_printing.database.repository;

import com.byko.api_3d_printing.database.ImageData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImagesRepository extends MongoRepository<ImageData, String> {

    Optional<ImageData> findById(String id);

}
