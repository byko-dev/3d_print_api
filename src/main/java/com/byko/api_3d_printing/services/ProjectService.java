package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.database.ConfigurationData;
import com.byko.api_3d_printing.database.MessageData;
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
    private MessageService messageService;

    private FileService fileService;


    public ProjectService(ProjectsRepository projectsRepository, MailService mailService,
                          ConfigurationService configurationService,
                          MessageService messageService, FileService fileService){
        this.projectsRepository = projectsRepository;
        this.mailService = mailService;
        this.messageService = messageService;
        this.configurationService = configurationService;
        this.fileService = fileService;
    }

    public String create(MultipartFile file, String nameAndLastName, String email, String phoneNumber, String description, String address, String ipAddress){
        RandomString random = new RandomString(14);
        String randomStr = random.nextString();
        ProjectsData projectsData = new ProjectsData();

        if(file != null){

            //TODO: need refactoring this shit and check out
            String id = fileService.uploadFile(file);

            //save uploaded file to directory
            //String fileName = Utils.saveFile(randomStr, file, FILE_DIRECTORY);

            //projectsData.setDownloadProjectFileLink(Utils.getDownloadLink(randomStr, fileName, DOMAIN_URL));
            projectsData.setProjectFileId(id);
            projectsData.setFileName(file.getName());
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


        MessageData messageData = new MessageData();
        messageData.setData(new Date().toString());
        messageData.setConversationId(projectId);
        messageData.setUserType(userType);
        messageData.setIpAddress(ipAddress);


        if(multipartFile.isEmpty()){

            String id = fileService.uploadFile(multipartFile);

            messageData.setFileId(id);
            messageData.setFileName(multipartFile.getName());

            //String fileName = Utils.saveFile(projectId, multipartFile, FILE_DIRECTORY);

            //conversationData.setFile(fileName);
            //conversationData.setDownloadFileLink(Utils.getDownloadLink(projectId, fileName, DOMAIN_URL));
        }

        if(description != null){
            messageData.setDescription(description);
        }

        messageService.save(messageData);

        return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
    }

    public List<ConversationResponse> getConversations(String projectId){
        List<ConversationResponse> conversationResponseList = new ArrayList<>();

        ProjectsData projectsData = projectsRepository.findByConversationKey(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not exist!"));

        conversationResponseList.add(new ConversationResponse(projectsData.getId(), projectsData.getProjectFileId(),
                projectsData.getDescription(), User.USER,
                projectsData.getDate(), projectsData.getFileName(), projectsData.getNameAndLastName()));

        List<MessageData> conversationDataList = messageService.getByConversationId(projectId);

        for(MessageData conversationData : conversationDataList){
            conversationResponseList.add(new ConversationResponse(conversationData.getId(),
                    conversationData.getFileId(), conversationData.getDescription(),
                    conversationData.getUserType(), conversationData.getData(),
                    conversationData.getFileName(), projectsData.getNameAndLastName()));
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
