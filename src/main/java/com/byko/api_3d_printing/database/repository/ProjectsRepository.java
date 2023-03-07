package com.byko.api_3d_printing.database.repository;

import com.byko.api_3d_printing.database.ProjectsDAO;
import com.byko.api_3d_printing.database.enums.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectsRepository extends MongoRepository<ProjectsDAO, String> {

    Optional<ProjectsDAO> findByConversationKey(String conversationKey);
    List<ProjectsDAO> findByOrderStatus(ProjectStatus orderStatus);

}
