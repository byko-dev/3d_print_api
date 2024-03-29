package com.byko.api_3d_printing.web_controllers;

import com.byko.api_3d_printing.database.repository.*;
import com.byko.api_3d_printing.exceptions.BadRequestException;
import com.byko.api_3d_printing.exceptions.ResourceNotFoundException;
import com.byko.api_3d_printing.exceptions.UnauthorizedException;
import com.byko.api_3d_printing.services.*;
import com.byko.api_3d_printing.smtp.MailService;
import com.byko.api_3d_printing.utils.CaptchaValidation;
import com.byko.api_3d_printing.utils.Utils;
import com.byko.api_3d_printing.configuration.MongoUserDetails;
import com.byko.api_3d_printing.configuration.JwtUtils;
import com.byko.api_3d_printing.database.*;
import com.byko.api_3d_printing.database.enums.User;
import com.byko.api_3d_printing.model.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "*") // allow all origins
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminWebController {

    private BCryptPasswordEncoder encoder;
    private JwtUtils jwtUtils;
    private AuthenticationManager authenticationManager;
    private MongoUserDetails mongoUserDetails;
    private ImagesRepository imagesRepository;
    private MailService mailService;
    private CaptchaValidation captchaValidator;
    private ProjectService projectService;
    private AdminService adminService;
    private ConfigurationService configurationService;
    private MessageService messageService;
    private FileService fileService;

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public ResponseEntity<?> getLastTimeAdminActivity(){
        AdminDAO adminData = adminService.getLastActiveAdminData();
        Long lastTimeActive = System.currentTimeMillis() - adminData.getLastTimeActivity();

        return new ResponseEntity<>(new LastTimeActiveDTO((lastTimeActive/60000)), HttpStatus.OK);
    }

    @RequestMapping(value = "/token/valid", method = RequestMethod.GET)
    public ResponseEntity<?> checkTokenIsValid(HttpServletRequest request){
        return new ResponseEntity<>(new Status("OK", request.getServletPath()), HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> getJwtToken(@RequestBody LoginDTO loginRequest){
        if(!captchaValidator.isValid(loginRequest.captchaResponse()))
            throw new UnauthorizedException("Captcha verification error!");

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        }catch(BadCredentialsException e){
            throw new UnauthorizedException("Login failed!");
        }
        final UserDetails userDetails = mongoUserDetails.loadUserByUsername(loginRequest.username());
        final String jwt = jwtUtils.generateToken(userDetails);

        return new ResponseEntity<>(new AuthenticationDTO(jwt), HttpStatus.OK);
    }

    @RequestMapping(value = "/send/response", method = RequestMethod.POST)
    public ResponseEntity<?> sendResponseForClient(@RequestParam String projectid,
                                                   @RequestParam String description,
                                                   @RequestParam MultipartFile file,
                                                   HttpServletRequest request){
        return projectService.sendResponse(projectid, description, file, request.getRemoteAddr(), User.ADMIN, request.getServletPath());
    }

    @RequestMapping(value = "/projects/list", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectsList(){
        return new ResponseEntity<>(projectService.getAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/change/project/status", method = RequestMethod.PUT)
    public ResponseEntity<?> changeProjectStatus(@Valid @RequestBody ChangeStatusDTO changeStatusRequest, HttpServletRequest request){
        ProjectsDAO projectsData = projectService.getByConversationKey(changeStatusRequest.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project data was not found!"));

        projectService.setOrderStatus(projectsData, changeStatusRequest.newStatus());
        return new ResponseEntity<>(new Status(changeStatusRequest.newStatus().toString(), request.getServletPath()), HttpStatus.OK);
    }

    @RequestMapping(value = "/change/password", method = RequestMethod.PUT)
    public ResponseEntity<?> adminChangePasswordRequest(@RequestBody ChangePasswordDTO changePasswordRequest, HttpServletRequest request){
        AdminDAO adminData = adminService.getAdminAccount(request);

        if(!encoder.matches(changePasswordRequest.password(), adminData.getPassword()))
            throw new BadRequestException("Passwords don't match");

        adminService.changePassword(adminData, changePasswordRequest.password());

        return new ResponseEntity<>(new Status("OK", request.getServletPath()), HttpStatus.OK);
    }

    @RequestMapping(value = "/remove/project", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeProject(@RequestParam("projectid") String projectId, HttpServletRequest request){
        ProjectsDAO projectsData = projectService.getByConversationKey(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project data was not found!"));


        //TODO: do something great

        //Utils.removeProjectDirectory(FILE_DIRECTORY, projectsData.getConversationKey());
        messageService.deleteAllByConversationId(projectsData.getConversationKey());
        projectService.delete(projectsData);
        return new ResponseEntity<>(new Status("OK", request.getServletPath()), HttpStatus.OK);
    }

    @RequestMapping(value = "/image/add", method = RequestMethod.POST)
    public ResponseEntity<?> addNewImage(@RequestParam(required = false) String description,
                                         @RequestParam String title,
                                         @RequestParam(required = false) String alt,
                                         @RequestParam MultipartFile file,
                                         HttpServletRequest request){

        //required image and title
        if(file == null || title == null)
            throw new BadRequestException("Image and title of your project is required!");

        ImageDAO imageData = new ImageDAO();

        imageData.setDescription(description);
        imageData.setTitle(title);
        imageData.setDate(Utils.getCurrentDate());
        imageData.setImageAlt(alt);

        String id = fileService.uploadFile(file);

        imageData.setFileId(id);

        imagesRepository.save(imageData);
        return new ResponseEntity<>(new Status("OK", request.getServletPath()), HttpStatus.OK);
    }

    @RequestMapping(value = "/image/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteImage(@RequestParam("imageid") String imageId, HttpServletRequest request){
        ImageDAO imageData = imagesRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image was not found!"));

        fileService.deleteFile(imageId);
        imagesRepository.delete(imageData);
        return new ResponseEntity<>(new Status("OK", request.getServletPath()), HttpStatus.OK);
    }

    @RequestMapping(value = "/image/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateImageData(@RequestParam String id,
                                             @RequestParam String description,
                                             @RequestParam String title,
                                             @RequestParam String alt,
                                             @RequestParam boolean changeDate,
                                             HttpServletRequest request){

        ImageDAO imageData = imagesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image was not found!"));

        if(title != null && !title.equals("")) imageData.setTitle(title);
        if(description != null && !description.equals("")) imageData.setDescription(description);
        if(alt != null && !alt.equals("")) imageData.setImageAlt(alt);

        if(changeDate == true) imageData.setDate(Utils.getCurrentDate());

        imagesRepository.save(imageData);

        return new ResponseEntity<>(new Status("OK", request.getServletPath()), HttpStatus.OK);
    }

    @RequestMapping(value = "/configuration/update", method = RequestMethod.POST)
    public ResponseEntity<?> changeConfiguration(@RequestBody ConfigurationUpdateDTO configuration, HttpServletRequest request){
        ConfigurationDAO data = configurationService.get()
                .orElse(configurationService.createEmptyConfiguration());

        if(!configuration.email().equals(""))
            data.setEmail(configuration.email());
        if(!configuration.password().equals(""))
            data.setEmailPass(configuration.password());
        if(configuration.enabled() != data.isEmailEnable())
            data.setEmailEnable(configuration.enabled());

        configurationService.save(data);

        return new ResponseEntity<>(new Status("OK", request.getServletPath()), HttpStatus.OK);
    }
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public ResponseEntity<?> getConfiguration(){
        ConfigurationDAO data = configurationService.get()
                .orElseThrow(() -> new ResourceNotFoundException("Configuration was not found!"));

        return new ResponseEntity<>(new ConfigurationDTO(data.getEmail(), data.isEmailEnable()), HttpStatus.OK);
    }

    @RequestMapping(value = "/check/smtp", method = RequestMethod.GET)
    public ResponseEntity<?> getSmtpStatus(HttpServletRequest request){
        ConfigurationDAO data = configurationService.get()
                .orElseThrow(() -> new ResourceNotFoundException("Configuration was not found!"));

        if(!data.isEmailEnable())
            throw new BadRequestException("Mail service is disabled!");

        try {
            mailService.checkStmpStatusMessage();
            return new ResponseEntity<>(new Status("Mail service is working well.", request.getServletPath()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Status("Mail service isn't working.", request.getServletPath()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
