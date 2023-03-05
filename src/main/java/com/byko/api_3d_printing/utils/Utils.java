package com.byko.api_3d_printing.utils;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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
}
