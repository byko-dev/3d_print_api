package com.byko.api_3d_printing.web_controllers;

import com.byko.api_3d_printing.database.*;
import com.byko.api_3d_printing.model.ProjectUserdata;
import com.byko.api_3d_printing.smtp.MailService;
import com.byko.api_3d_printing.utils.RandomString;
import com.byko.api_3d_printing.utils.Utils;
import com.byko.api_3d_printing.database.enums.User;
import com.byko.api_3d_printing.model.ConversationResponse;
import com.byko.api_3d_printing.model.StatusModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*") // allow all origins
@RestController
public class WebController {

    @Value("${file.upload-dir}")
    private String FILE_DIRECTORY;

    @Value("${domain.url}")
    private String DOMAIN_URL;

    @Value("${file.image-dir}")
    private String IMAGES_DIRECTORY;

    @Value("${captcha.secrect.key}")
    private String captchaSecretKey;

    @Value("${front.url}")
    private String frontendUrl;

    private ConversationRepository conversationRepository;
    private ProjectsRepository projectsRepository;
    private ImagesRepository imagesRepository;
    private MailService mailService;
    private ConfigurationRepository configurationRepository;

    public WebController(ConversationRepository conversationRepository, ProjectsRepository projectsRepository,
                         ImagesRepository imagesRepository, MailService mailService, ConfigurationRepository configurationRepository){
        this.conversationRepository = conversationRepository;
        this.projectsRepository = projectsRepository;
        this.imagesRepository = imagesRepository;
        this.mailService = mailService;
        this.configurationRepository = configurationRepository;
    }

    @RequestMapping(value = "/create/project", method = RequestMethod.POST)
    public ResponseEntity<?> createConversationLink(MultipartHttpServletRequest request){

        if(Utils.captchaValidator(request.getParameter("captcha"), captchaSecretKey)){
            String nameAndLastName = request.getParameter("nameAndLastName");
            String email = request.getParameter("email");
            String address = request.getParameter("address");
            String phoneNumber = request.getParameter("phoneNumber");
            String description = request.getParameter("description");
            MultipartFile multipartFile = request.getFile("file");


            RandomString random = new RandomString(14);
            String randomStr = random.nextString();
            ProjectsData projectsData = new ProjectsData();

            if(multipartFile != null){
                //save uploaded file to directory
                String fileName = Utils.saveFile(randomStr, multipartFile, FILE_DIRECTORY);

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
            projectsData.setIpAddress(request.getRemoteAddr());

            projectsRepository.save(projectsData);

            ConfigurationData data = configurationRepository.findAll().get(0);

            if(data != null && data.isEmailEnable() && Utils.regexMail(email)){
                try {
                    mailService.sendMessageWithLink(email, frontendUrl + "project?projectid=" + randomStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return new ResponseEntity<>(new StatusModel(randomStr) , HttpStatus.OK); //do not return full link, it will be constructed in frontend
        }
        return new ResponseEntity<>(new StatusModel("Captcha verification error!"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<?> downloadFiles(@RequestParam("file") String fileStr, @RequestParam("key") String objectId){
        try {
            File file = new File(FILE_DIRECTORY + "/" + objectId + "/" + fileStr);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/send/response", method = RequestMethod.POST)
    public ResponseEntity<?> sendResponseToProject(MultipartHttpServletRequest request){
        if(Utils.captchaValidator(request.getParameter("captcha"), captchaSecretKey)){
            return Utils.sendResponse(request, projectsRepository, conversationRepository, FILE_DIRECTORY,
                    DOMAIN_URL, User.USER);
        }
        return new ResponseEntity<>(new StatusModel("Captcha verification error!"), HttpStatus.UNAUTHORIZED);
    }


    @RequestMapping(value = "/project/conversation", method = RequestMethod.GET)
    public ResponseEntity<?> getConversationData(@RequestParam("projectid") String projectId){

        List<ConversationResponse> conversationResponseList = new ArrayList<>();

        ProjectsData projectsData = projectsRepository.findByConversationKey(projectId);

        if(projectsData != null){
            conversationResponseList.add(new ConversationResponse(projectsData.getId(), projectsData.getProjectFile(),
                    projectsData.getDescription(), User.USER, projectsData.getDownloadProjectFileLink(),
                    projectsData.getDate(), projectsData.getProjectFile(), projectsData.getNameAndLastName()));

            List<ConversationData> conversationDataList = conversationRepository.findByConversationId(projectId);

            for(ConversationData conversationData : conversationDataList){
                conversationResponseList.add(new ConversationResponse(conversationData.getId(),
                        conversationData.getFile(), conversationData.getDescription(),
                        conversationData.getUserType(), conversationData.getDownloadFileLink(),
                        conversationData.getData(), conversationData.getFile(),
                        projectsData.getNameAndLastName()));
            }
            return new ResponseEntity<>(conversationResponseList, HttpStatus.OK);
        }
        return new ResponseEntity<>(projectId, HttpStatus.OK);
    }


    @RequestMapping(value = "/project/data")
    public ResponseEntity<?> getProjectInformation(@RequestParam("projectid") String projectId) {
        ProjectsData projectsData = projectsRepository.findByConversationKey(projectId);
        ProjectUserdata userData = new ProjectUserdata(projectsData.getNameAndLastName(),
                projectsData.getAddress(),
                projectsData.getNumberPhone(), projectsData.getEmail(), projectsData.getDate(), projectsData.getOrderStatus() );
        return new ResponseEntity<>(projectsData, projectsData != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/images", method = RequestMethod.GET)
    public ResponseEntity<?> getAllImages(){
        return new ResponseEntity<>(imagesRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity<?> getImageById(@RequestParam("imageid") String imageId){
        Optional<ImageData> imageData = imagesRepository.findById(imageId);
        if(imageData.isPresent()){
            ByteArrayResource inputStream = null;
            try {
                inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get(
                        IMAGES_DIRECTORY + imageData.get().getImageFileName())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(inputStream, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new StatusModel("Image doesn't exists"), HttpStatus.BAD_REQUEST);
        }
    }
}
