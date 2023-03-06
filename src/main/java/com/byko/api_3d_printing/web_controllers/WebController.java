package com.byko.api_3d_printing.web_controllers;

import com.byko.api_3d_printing.database.*;
import com.byko.api_3d_printing.database.repository.ImagesRepository;
import com.byko.api_3d_printing.exceptions.ResourceNotFoundException;
import com.byko.api_3d_printing.exceptions.UnauthorizedException;
import com.byko.api_3d_printing.model.LoadFile;
import com.byko.api_3d_printing.model.Status;
import com.byko.api_3d_printing.services.FileService;
import com.byko.api_3d_printing.services.ProjectService;
import com.byko.api_3d_printing.utils.CaptchaValidation;
import com.byko.api_3d_printing.database.enums.User;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.Optional;

@CrossOrigin(origins = "*") // allow all origins
@RestController
@RequestMapping("/user")
@Validated
@AllArgsConstructor
public class WebController {

    private ImagesRepository imagesRepository;
    private CaptchaValidation captchaValidator;
    private ProjectService projectService;
    private FileService fileService;

    @RequestMapping(value = "/create/project", method = RequestMethod.POST)
    public ResponseEntity<?> createConversationLink(@Valid @NotNull @NotEmpty @RequestParam String captcha,
                                                    @RequestParam String nameAndLastName,
                                                    @Valid @Email @RequestParam String email,
                                                    @RequestParam String address,
                                                    @Valid @NumberFormat @RequestParam String phoneNumber,
                                                    @RequestParam String description,
                                                    @RequestParam MultipartFile file,
                                                    HttpServletRequest request){

        if(!captchaValidator.isValid(captcha))
            throw new UnauthorizedException("Captcha verification error!");

        //do not return full link, it will be constructed in frontend
        return new ResponseEntity<>(new Status(projectService.create(file, nameAndLastName, email, phoneNumber,
                description, address, request.getRemoteAddr()), request.getServletPath()) , HttpStatus.OK);

    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<?> downloadFiles(@RequestParam("id") String fileId){
        LoadFile loadFile = fileService.download(fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + loadFile.getFilename());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType()))
                .headers(headers)
                .contentLength(Long.parseLong(loadFile.getFileSize()))
                .body(new ByteArrayResource(loadFile.getFile()));
    }

    @RequestMapping(value = "/send/response", method = RequestMethod.POST)
    public ResponseEntity<?> sendResponseToProject(@Valid @NotNull @NotEmpty @RequestParam String captcha,
                                                   @RequestParam String projectid,
                                                   @RequestParam String description,
                                                   @RequestParam MultipartFile file,
                                                   HttpServletRequest request){
        if(!captchaValidator.isValid(captcha))
            throw new UnauthorizedException("Captcha verification error!");

        return projectService.sendResponse(projectid, description, file, request.getRemoteAddr(), User.USER);
    }


    @RequestMapping(value = "/project/conversation", method = RequestMethod.GET)
    public ResponseEntity<?> getConversationData(@RequestParam("projectid") String projectId){
        return new ResponseEntity<>(projectService.getConversations(projectId), HttpStatus.OK);
    }


    @RequestMapping(value = "/project/data")
    public ResponseEntity<?> getProjectInformation(@RequestParam("projectid") String projectId) {
        ProjectsData projectsData = projectService.getByConversationKey(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project data was not found!"));

        return new ResponseEntity<>(projectsData, HttpStatus.OK );
    }

    @RequestMapping(value = "/images", method = RequestMethod.GET)
    public ResponseEntity<?> getAllImages(){
        return new ResponseEntity<>(imagesRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity<?> getImageById(@RequestParam("imageid") String imageId){
        Optional<ImageData> imageData = imagesRepository.findById(imageId);
        if(!imageData.isPresent())
            throw new ResourceNotFoundException("Image was not found!");

        LoadFile loadFile = fileService.download(imageId);

        ByteArrayResource inputStream = null;
        /*try {
            inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get(
                    IMAGES_DIRECTORY + imageData.get().getImageFileName())));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return new ResponseEntity<>(new ByteArrayResource(loadFile.getFile()), HttpStatus.OK);
    }
}
