package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.model.LoadFile;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public String uploadFile(MultipartFile upload){

        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", upload.getSize());

        Object fileID = null;
        try {
            fileID = gridFsTemplate.store(upload.getInputStream(), upload.getOriginalFilename(), upload.getContentType(), metadata);
        } catch (IOException e) {

            //TODO: need new Exception in Exception Controller
            throw new RuntimeException(e);

        }

        return fileID.toString();
    }

    public LoadFile download(String fileId){
        GridFSFile gridFSFile = gridFsTemplate.findOne( new Query(Criteria.where("_id").is(fileId)) );

        LoadFile loadFile = new LoadFile();

        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            loadFile.setFilename( gridFSFile.getFilename() );

            loadFile.setFileType( gridFSFile.getMetadata().get("_contentType").toString() );

            loadFile.setFileSize( gridFSFile.getMetadata().get("fileSize").toString() );

            try {
                loadFile.setFile(operations.getResource(gridFSFile).getInputStream().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return loadFile;
    }

    public void deleteFile(String fileId){
        gridFsTemplate.delete(query(where("_id").is(fileId)));
    }


}
