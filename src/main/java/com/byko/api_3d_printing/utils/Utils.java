package com.byko.api_3d_printing.utils;

import com.byko.api_3d_printing.database.ConversationData;
import com.byko.api_3d_printing.database.ConversationRepository;
import com.byko.api_3d_printing.database.ProjectsRepository;
import com.byko.api_3d_printing.database.enums.User;
import com.byko.api_3d_printing.model.RecaptchaResult;
import com.byko.api_3d_printing.model.StatusModel;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static String createDir(String path, String objectId){
        File file = new File(path + objectId);
        if(!file.exists()) file.mkdir();
        return file.getAbsolutePath();
    }

    public static String saveFile(String objectId, MultipartFile multipartFile, String FILE_DIRECTORY){
        RandomString random = new RandomString(16);
        String fileName = multipartFile.getOriginalFilename();

        //change original name to random str
        Optional<String> extension = getFileExtension(multipartFile.getOriginalFilename());
        if(extension.isPresent()){
            fileName = random.nextString() + "." + extension.get();
        }

        File myFile = new File(createDir(FILE_DIRECTORY, objectId.toString()) +"/"+ fileName);
        try {
            myFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(myFile);
            fos.write(multipartFile.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static String getDownloadLink(String objectId, String fileName, String DOMAIN_URL){
        return DOMAIN_URL + "download?file=" + fileName + "&key=" + objectId;
    }

    public static ResponseEntity<?> sendResponse(MultipartHttpServletRequest request, ProjectsRepository projectsRepository,
                                                 ConversationRepository conversationRepository, String FILE_DIRECTORY,
                                                 String DOMAIN_URL, User userType){

        String projectId = request.getParameter("projectid");
        String description = request.getParameter("description");
        MultipartFile multipartFile = request.getFile("file");

        if(projectId != null && projectsRepository.findByConversationKey(projectId) != null &&
                (description != null || multipartFile != null)) {

            ConversationData conversationData = new ConversationData();
            conversationData.setData(new Date().toString());
            conversationData.setConversationId(projectId);
            conversationData.setUserType(userType);
            conversationData.setIpAddress(request.getRemoteAddr());

            if(multipartFile != null){
                String fileName = Utils.saveFile(projectId, multipartFile, FILE_DIRECTORY);

                conversationData.setFile(fileName);
                conversationData.setDownloadFileLink(Utils.getDownloadLink(projectId, fileName, DOMAIN_URL));
            }

            if(description != null){
                conversationData.setDescription(description);
            }

            conversationRepository.save(conversationData);

            return new ResponseEntity<>(new StatusModel("OK"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new StatusModel("BAD REQUEST"), HttpStatus.OK);
    }

    public static String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static String saveImages(String IMAGES_DIRECTORY, MultipartFile multipartFile, String newFileName){
        String fileName = newFileName;
        Optional<String> extension = getFileExtension(multipartFile.getOriginalFilename());
        if(extension.isPresent()){
            fileName = newFileName + "." + extension.get();
        }

        File myFile = new File(IMAGES_DIRECTORY + fileName);

        try {
            myFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(myFile);
            fos.write(multipartFile.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public static Optional<String> getFileExtension(String file_name){
        return Optional.ofNullable(file_name)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(file_name.indexOf(".") + 1));
    }

    public static void deleteImageFile(String imageDirectory, String projectFile){
        File file = new File(imageDirectory + projectFile);

        if(file.exists()){
            file.delete();
        }
    }

    public static void removeProjectDirectory(String projectsDirectory ,String projectsFolderName) {
        File file = new File(projectsDirectory + projectsFolderName);

        if(file.exists()){
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean captchaValidator(String captchaResponse, String secretKey){
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + secretKey + "&response=" + captchaResponse;

        RestTemplate restTemplate = new RestTemplate();
        RecaptchaResult result = restTemplate.getForObject(url, RecaptchaResult.class);

        System.out.println("Captcha results " + result.getScore() + " " + result.getChallenge_ts());

        if(result.isSuccess()){
            if(0.5 <= result.getScore()) return true;
            else return false;
        }
        return false;
    }

    public static Boolean regexMail(String email){
        Pattern patternMail = Pattern.compile("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$");
        Matcher matcherMail = patternMail.matcher(email);
        return matcherMail.matches();
    }
}
