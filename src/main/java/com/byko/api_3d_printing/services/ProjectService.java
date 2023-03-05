package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.database.ConfigurationData;
import com.byko.api_3d_printing.database.ConversationData;
import com.byko.api_3d_printing.database.ProjectsData;
import com.byko.api_3d_printing.database.enums.User;
import com.byko.api_3d_printing.database.repository.ProjectsRepository;
import com.byko.api_3d_printing.exceptions.BadRequestException;
import com.byko.api_3d_printing.exceptions.ResourceNotFoundException;
import com.byko.api_3d_printing.model.ConversationResponse;
import com.byko.api_3d_printing.model.StatusModel;
import com.byko.api_3d_printing.smtp.MailService;
import com.byko.api_3d_printing.utils.RandomString;
import com.byko.api_3d_printing.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {


    @Value("${file.upload-dir}")
    private String FILE_DIRECTORY;

    @Value("${domain.url}")
    private String DOMAIN_URL;

    @Value("${front.url}")
    private String FRONTEND_URL;

    private final ProjectsRepository projectsRepository;
    private final MailService mailService;
    private ConfigurationService configurationService;
    private ConversationService conversationService;


    public ProjectService(ProjectsRepository projectsRepository, MailService mailService,
                          ConfigurationService configurationService,
                          ConversationService conversationService){
        this.projectsRepository = projectsRepository;
        this.mailService = mailService;
        this.conversationService = conversationService;
        this.configurationService = configurationService;
    }

    public String create(MultipartFile file, String nameAndLastName, String email, String phoneNumber, String description, String address, String ipAddress){
        RandomString random = new RandomString(14);
        String randomStr = random.nextString();
        ProjectsData projectsData = new ProjectsData();

        if(file != null){
            //save uploaded file to directory
            String fileName = Utils.saveFile(randomStr, file, FILE_DIRECTORY);

            projectsData.setDownloadProjectFileLink(Utils.getDownloadLink(randomStr, fileName, DOMAIN_URL));
            projectsData.setProjectFile(fileName);
        }

        projectsData.setDate(Utils.getCurrentDate());
        projectsData.setConversationKey(randomStr);
        projectsData.setNameAndLastName(nameAndLastName);
        projectsData.setEmail(email);
        projectsData.setNumberPhone(phoneNumber);
        projectsData.setDescription(description);
        projectsData.setAddress(address);
        projectsData.setOrderStatus(0);
        projectsData.setIpAddress(ipAddress);

        projectsRepository.save(projectsData);

        ConfigurationData data = configurationService.get().orElseThrow(() -> new ResourceNotFoundException("Configuration was not found!"));

        if(data.isEmailEnable()){
            try {
                mailService.sendMessageWithLink(email, FRONTEND_URL + "project?projectid=" + randomStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return randomStr;
    }


    public ResponseEntity<?> sendResponse(String projectId, String description, MultipartFile multipartFile,
                                          String ipAddress, User userType){

        if(!projectsRepository.findByConversationKey(projectId).isPresent())
            throw new ResourceNotFoundException("Project was not found!");

        if(description == null && multipartFile == null)
            throw new BadRequestException("One of properties such as file or content can not be null!");


        ConversationData conversationData = new ConversationData();
        conversationData.setData(new Date().toString());
        conversationData.setConversationId(projectId);
        conversationData.setUserType(userType);
        conversationData.setIpAddress(ipAddress);

        if(multipartFile.isEmpty()){
            String fileName = Utils.saveFile(projectId, multipartFile, FILE_DIRECTORY);

            conversationData.setFile(fileName);
            conversationData.setDownloadFileLink(Utils.getDownloadLink(projectId, fileName, DOMAIN_URL));
        }

        if(description != null){
            conversationData.setDescription(description);
        }

        conversationService.save(conversationData);

        return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
    }

    public List<ConversationResponse> getConversations(String projectId){
        List<ConversationResponse> conversationResponseList = new ArrayList<>();

        ProjectsData projectsData = projectsRepository.findByConversationKey(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not exist!"));

        conversationResponseList.add(new ConversationResponse(projectsData.getId(), projectsData.getProjectFile(),
                projectsData.getDescription(), User.USER, projectsData.getDownloadProjectFileLink(),
                projectsData.getDate(), projectsData.getProjectFile(), projectsData.getNameAndLastName()));

        List<ConversationData> conversationDataList = conversationService.getByConversationId(projectId);

        for(ConversationData conversationData : conversationDataList){
            conversationResponseList.add(new ConversationResponse(conversationData.getId(),
                    conversationData.getFile(), conversationData.getDescription(),
                    conversationData.getUserType(), conversationData.getDownloadFileLink(),
                    conversationData.getData(), conversationData.getFile(),
                    projectsData.getNameAndLastName()));
        }
        return conversationResponseList;
    }

    public Optional<ProjectsData> getByConversationKey(String conversationKey){
        return projectsRepository.findByConversationKey(conversationKey);
    }

    public void setOrderStatus(ProjectsData projectsData, int orderStatus){
        projectsData.setOrderStatus(orderStatus);
        projectsRepository.save(projectsData);
    }

    public List<ProjectsData> getAll(){
        return projectsRepository.findAll();
    }

    public void delete(ProjectsData projectsData){
        projectsRepository.delete(projectsData);
    }



}
