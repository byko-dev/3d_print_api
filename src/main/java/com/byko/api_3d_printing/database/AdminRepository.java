package com.byko.api_3d_printing.database;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<AdminData, String> {

    AdminData findByUsername(String username);
    AdminData findTopByLastTimeActivity();
    AdminData findFirstByOrderByLastTimeActivityDesc();

}
