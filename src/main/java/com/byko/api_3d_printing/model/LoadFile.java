package com.byko.api_3d_printing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoadFile {

    private String filename;
    private String fileType;
    private String fileSize;
    private byte[] file;

}
