package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.database.ConfigurationDAO;
import com.byko.api_3d_printing.database.MessageDAO;
import com.byko.api_3d_printing.database.ProjectsDAO;
import com.byko.api_3d_printing.database.enums.User;
import com.byko.api_3d_printing.database.repository.ProjectsRepository;
import com.byko.api_3d_printing.exceptions.BadRequestException;
import com.byko.api_3d_printing.exceptions.ResourceNotFoundException;
import com.byko.api_3d_printing.model.MessageDTO;
import com.byko.api_3d_printing.model.MessageDTOMapper;
import com.byko.api_3d_printing.model.Status;
import com.byko.api_3d_printing.smtp.MailService;
import com.byko.api_3d_printing.utils.Utils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {



    @Value("${front.url}")
    private String FRONTEND_URL;

    private final ProjectsRepository projectsRepository;
    private final MailService mailService;
    private ConfigurationService configurationService;
    private MessageService messageService;

    private FileService fileService;

    private MessageDTOMapper messageDTOMapper;


    public ProjectService(ProjectsRepository projectsRepository, MailService mailService,
                          ConfigurationService configurationService,
                          MessageService messageService, FileService fileService, MessageDTOMapper messageDTOMapper){
        this.projectsRepository = projectsRepository;
        this.mailService = mailService;
        this.messageService = messageService;
        this.configurationService = configurationService;
        this.fileService = fileService;
        this.messageDTOMapper = messageDTOMapper;
    }

    public String create(MultipartFile file, String nameAndLastName, String email, String phoneNumber, String description, String address, String ipAddress){
        ProjectsDAO projectsData = new ProjectsDAO();

        ObjectId conversation_key = new ObjectId();

        if(file != null){

            String id = fileService.uploadFile(file);

            projectsData.setProjectFileId(id);
            projectsData.setFileName(file.getName());
        }

        projectsData.setDate(Utils.getCurrentDate());
        projectsData.setConversationKey(conversation_key.toString());
        projectsData.setNameAndLastName(nameAndLastName);
        projectsData.setEmail(email);
        projectsData.setNumberPhone(phoneNumber);
        projectsData.setDescription(description);
        projectsData.setAddress(address);
        projectsData.setOrderStatus(0);
        projectsData.setIpAddress(ipAddress);


        projectsRepository.save(projectsData);

        ConfigurationDAO data = configurationService.get().orElseThrow(() -> new ResourceNotFoundException("Configuration was not found!"));

        if(data.isEmailEnable()){
            try {
                mailService.sendMessageWithLink(email, FRONTEND_URL + "project?projectid=" + conversation_key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return conversation_key.toString();
    }


    public ResponseEntity<?> sendResponse(String projectId, String description, MultipartFile multipartFile,
                                          String ipAddress, User userType, String path){

        if(!projectsRepository.findByConversationKey(projectId).isPresent())
            throw new ResourceNotFoundException("Project was not found!");

        if(description == null && multipartFile == null)
            throw new BadRequestException("One of properties such as file or content can not be null!");


        MessageDAO messageData = new MessageDAO();
        messageData.setData(new Date().toString());
        messageData.setConversationId(projectId);
        messageData.setUserType(userType);
        messageData.setIpAddress(ipAddress);


        if(multipartFile.isEmpty()){

            String id = fileService.uploadFile(multipartFile);

            messageData.setFileId(id);
            messageData.setFileName(multipartFile.getName());
        }

        if(description != null){
            messageData.setDescription(description);
        }

        messageService.save(messageData);

        return new ResponseEntity<>(new Status("OK", path), HttpStatus.OK);
    }

    public List<MessageDTO> getConversations(String projectId){
        List<MessageDTO> messageList = new ArrayList<>();

        ProjectsDAO projectsData = projectsRepository.findByConversationKey(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not exist!"));

        messageList.add(new MessageDTO(projectsData.getId(),
                projectsData.getProjectFileId(),
                projectsData.getDescription(),
                User.USER,
                projectsData.getDate(),
                projectsData.getFileName(),
                projectsData.getNameAndLastName()));

        messageList
                .addAll(messageService.getByConversationId(projectId).stream().map(messageDTOMapper).collect(Collectors.toList()));
        return messageList;
    }

    public Optional<ProjectsDAO> getByConversationKey(String conversationKey){
        return projectsRepository.findByConversationKey(conversationKey);
    }

    public void setOrderStatus(ProjectsDAO projectsData, int orderStatus){
        projectsData.setOrderStatus(orderStatus);
        projectsRepository.save(projectsData);
    }

    public List<ProjectsDAO> getAll(){
        return projectsRepository.findAll();
    }

    public void delete(ProjectsDAO projectsData){
        projectsRepository.delete(projectsData);
    }



}
