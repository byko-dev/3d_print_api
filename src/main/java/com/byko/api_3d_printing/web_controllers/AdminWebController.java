package com.byko.api_3d_printing.web_controllers;

import com.byko.api_3d_printing.database.repository.*;
import com.byko.api_3d_printing.exceptions.ResourceNotFoundException;
import com.byko.api_3d_printing.exceptions.UnauthorizedException;
import com.byko.api_3d_printing.services.ConfigurationService;
import com.byko.api_3d_printing.services.ProjectService;
import com.byko.api_3d_printing.smtp.MailService;
import com.byko.api_3d_printing.utils.CaptchaValidation;
import com.byko.api_3d_printing.utils.RandomString;
import com.byko.api_3d_printing.utils.Utils;
import com.byko.api_3d_printing.configuration.MongoUserDetails;
import com.byko.api_3d_printing.configuration.jwt.JwtUtils;
import com.byko.api_3d_printing.database.*;
import com.byko.api_3d_printing.database.enums.User;
import com.byko.api_3d_printing.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@CrossOrigin(origins = "*") // allow all origins
@RestController
@RequestMapping("/admin")
public class AdminWebController {

    @Value("${file.upload-dir}")
    private String FILE_DIRECTORY;

    @Value("${file.image-dir}")
    private String IMAGES_DIRECTORY;
    private AdminRepository adminRepository;
    private ConversationRepository conversationRepository;
    private ProjectsRepository projectsRepository;
    private BCryptPasswordEncoder encoder;
    private JwtUtils jwtUtils;
    private AuthenticationManager authenticationManager;
    private MongoUserDetails mongoUserDetails;
    private ImagesRepository imagesRepository;
    private ConfigurationRepository configurationRepository;
    private MailService mailService;
    private CaptchaValidation captchaValidator;

    private ProjectService projectService;
    private ConfigurationService configurationService;


    public AdminWebController(AdminRepository adminRepository, ConversationRepository conversationRepository,
                              ProjectsRepository projectsRepository, BCryptPasswordEncoder encoder, JwtUtils jwtUtils,
                              AuthenticationManager authenticationManager, MongoUserDetails mongoUserDetails,
                              ImagesRepository imagesRepository, ConfigurationRepository configurationRepository,
                              MailService mailService, CaptchaValidation captchaValidator, ProjectService projectService){
        this.adminRepository = adminRepository;
        this.conversationRepository = conversationRepository;
        this.projectsRepository = projectsRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.mongoUserDetails = mongoUserDetails;
        this.imagesRepository = imagesRepository;
        this.configurationRepository = configurationRepository;
        this.mailService = mailService;
        this.captchaValidator = captchaValidator;
        this.projectService = projectService;
    }

    private AdminData getAdminAccount(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");
        String username = jwtUtils.extractUsername(authorizationHeader);
        AdminData adminData = adminRepository.findByUsername(username);
        return adminData;
    }

    private void setAdminLastActivity(AdminData adminData){
        adminData.setLastTimeActivity(System.currentTimeMillis());
        adminRepository.save(adminData);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public ResponseEntity<?> getLastTimeAdminActivity(){
        AdminData adminData = adminRepository.findFirstByOrderByLastTimeActivityDesc();
        Long lastTimeActive = System.currentTimeMillis() - adminData.getLastTimeActivity();

        return new ResponseEntity<>(new LastTimeActiveResponse((lastTimeActive/60000)), HttpStatus.OK);
    }

    @RequestMapping(value = "/token/valid", method = RequestMethod.GET)
    public ResponseEntity<?> checkTokenIsValid(){
        return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> getJwtToken(@RequestBody LoginRequest loginRequest){
        if(!captchaValidator.isValid(loginRequest.getCaptchaResponse()))
            throw new UnauthorizedException("Captcha verification error!");

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        }catch(BadCredentialsException e){
            throw new UnauthorizedException("Login failed!");
        }
        final UserDetails userDetails = mongoUserDetails.loadUserByUsername(loginRequest.getUsername());
        final String jwt = jwtUtils.generateToken(userDetails);

        setAdminLastActivity(adminRepository.findByUsername(userDetails.getUsername()));

        return new ResponseEntity<>(new AuthenticationResponse(jwt), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/send/response", method = RequestMethod.POST)
    public ResponseEntity<?> sendResponseForClient(@RequestParam String projectid,
                                                   @RequestParam String description,
                                                   @RequestParam MultipartFile file,
                                                   HttpServletRequest request){
        return projectService.sendResponse(projectid, description, file, request.getRemoteAddr(), User.ADMIN);
    }

    @RequestMapping(value = "/projects/list", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectsList(HttpServletRequest request){
        AdminData adminData = getAdminAccount(request);
        if (adminData != null) {
            setAdminLastActivity(adminData);

            return new ResponseEntity<>(projectsRepository.findAll(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new StatusModel("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/change/project/status", method = RequestMethod.PUT)
    public ResponseEntity<?> changeProjectStatus(@RequestBody ChangeStatusRequest changeStatusRequest, HttpServletRequest request){
        AdminData adminData = getAdminAccount(request);

        if(adminData != null){
            setAdminLastActivity(adminData);

            ProjectsData projectsData = projectsRepository.findByConversationKey(changeStatusRequest.projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project data was not found!"));

            if(changeStatusRequest.newStatus>=0){
                projectsData.setOrderStatus(changeStatusRequest.newStatus);
                projectsRepository.save(projectsData);
                return new ResponseEntity<>(new StatusModel(changeStatusRequest.newStatus.toString()), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new StatusModel("Project doesn't exists any more"), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(new StatusModel("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/change/password", method = RequestMethod.PUT)
    public ResponseEntity<?> adminChangePasswordRequest(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request){
        AdminData adminData = getAdminAccount(request);
        if(adminData != null){
            setAdminLastActivity(adminData);

            if(encoder.matches(changePasswordRequest.getPassword(), adminData.getPassword())){
                adminData.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
                adminRepository.save(adminData);

                return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new StatusModel("Wrong password"), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(new StatusModel("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/remove/project", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeProject(@RequestParam("projectid") String projectId, HttpServletRequest request){
        ProjectsData projectsData = projectsRepository.findByConversationKey(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project data was not found!"));

        Utils.removeProjectDirectory(FILE_DIRECTORY, projectsData.getConversationKey());
        conversationRepository.deleteAllByConversationId(projectsData.getConversationKey());
        projectsRepository.delete(projectsData);
        return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
    }

    @RequestMapping(value = "/image/add", method = RequestMethod.POST)
    public ResponseEntity<?> addNewImage(MultipartHttpServletRequest request){
        AdminData adminData = getAdminAccount(request);

        if(adminData != null){
            setAdminLastActivity(adminData);

            String description = request.getParameter("description");
            String title = request.getParameter("title");
            String imageAlt = request.getParameter("alt");
            MultipartFile multipartFile = request.getFile("file");

            if(multipartFile != null && title != null){ //required image and title
                ImageData imageData = new ImageData();

                imageData.setDescription(description);
                imageData.setTitle(title);
                imageData.setDate(Utils.getCurrentDate());
                imageData.setImageAlt(imageAlt);

                RandomString randomString = new RandomString(12);
                imageData.setImageFileName(
                        Utils.saveImages(IMAGES_DIRECTORY, multipartFile, randomString.nextString()));

                imagesRepository.save(imageData);
                return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new StatusModel("Image and title of your project is required!"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new StatusModel("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/image/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteImage(@RequestParam("imageid") String imageId, HttpServletRequest request){
        AdminData admin = getAdminAccount(request);
        if(admin != null){
            setAdminLastActivity(admin);

            Optional<ImageData> imageData = imagesRepository.findById(imageId);
            if(imageData.isPresent()){
                Utils.deleteImageFile(IMAGES_DIRECTORY, imageData.get().getImageFileName());
                imagesRepository.delete(imageData.get());
                return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new StatusModel("Image doesn't exists"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new StatusModel("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/image/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateImageData(MultipartHttpServletRequest request){
        AdminData adminData = getAdminAccount(request);

        if(adminData != null){
            setAdminLastActivity(adminData);

            String description = request.getParameter("description");
            String title = request.getParameter("title");
            String changeDate = request.getParameter("changeDate");
            String imageAlt = request.getParameter("alt");

            Optional<ImageData> imageData = imagesRepository.findById(request.getParameter("id"));

            if(imageData.isPresent()){
                if(title != null && !title.equals("")) imageData.get().setTitle(title);
                if(description != null && !description.equals("")) imageData.get().setDescription(description);
                if(imageAlt != null && !imageAlt.equals("")) imageData.get().setImageAlt(imageAlt);

                //I couldn't do this with boolean variable btw
                if(changeDate != null && changeDate.equals("true")) imageData.get().setDate(Utils.getCurrentDate());

                imagesRepository.save(imageData.get());

                return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new StatusModel("Image doesn't exists"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new StatusModel("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/configuration/update", method = RequestMethod.POST)
    public ResponseEntity<?> changeConfiguration(@RequestBody ConfigurationUpdate configuration, HttpServletRequest httpServletRequest){
        AdminData adminData = getAdminAccount(httpServletRequest);
        if(adminData != null){
            ConfigurationData data = configurationRepository.findAll().get(0);
            if(data != null){
                if(!configuration.getEmail().equals(""))
                    data.setEmail(configuration.getEmail());
                if(!configuration.getPassword().equals(""))
                    data.setEmailPass(configuration.getPassword());
                if(configuration.isEnabled() != data.isEmailEnable())
                    data.setEmailEnable(configuration.isEnabled());

                configurationRepository.save(data);

                return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new StatusModel("Configuration doesn't exists!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new StatusModel("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public ResponseEntity<?> getConfiguration(HttpServletRequest httpServletRequest){
        AdminData admin = getAdminAccount(httpServletRequest);

        if(admin != null){
            ConfigurationData data = configurationRepository.findAll().get(0);

            if(data != null) return new ResponseEntity<>(new ConfigurationResponse(data.getEmail(), data.isEmailEnable()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new StatusModel("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }
    @RequestMapping(value = "/check/smtp", method = RequestMethod.GET)
    public ResponseEntity<?> getSmtpStatus(HttpServletRequest httpServletRequest){
        ConfigurationData data = configurationRepository.findAll().get(0);

        if(data != null && data.isEmailEnable()){
            try {
                mailService.checkStmpStatusMessage();
                return new ResponseEntity<>(new StatusModel("Mail service is working well."), HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(new StatusModel("Mail service isn't working."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity<>(new StatusModel("Mail service is disabled!"), HttpStatus.OK);
        }
    }
}
