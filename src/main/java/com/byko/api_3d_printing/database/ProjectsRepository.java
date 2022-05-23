package com.byko.api_3d_printing.database;

import com.byko.api_3d_printing.database.enums.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectsRepository extends MongoRepository<ProjectsData, String> {

    ProjectsData findByConversationKey(String conversationKey);
    List<ProjectsData> findByOrderStatus(Status orderStatus);

}
