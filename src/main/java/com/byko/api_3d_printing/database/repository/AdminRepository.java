package com.byko.api_3d_printing.database.repository;

import com.byko.api_3d_printing.database.AdminDAO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<AdminDAO, String> {

    AdminDAO findByUsername(String username);
    AdminDAO findTopByLastTimeActivity();
    AdminDAO findFirstByOrderByLastTimeActivityDesc();

}
