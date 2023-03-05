package com.byko.api_3d_printing.database.repository;

import com.byko.api_3d_printing.database.ProjectsData;
import com.byko.api_3d_printing.database.enums.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectsRepository extends MongoRepository<ProjectsData, String> {

    Optional<ProjectsData> findByConversationKey(String conversationKey);
    List<ProjectsData> findByOrderStatus(ProjectStatus orderStatus);

}
