package com.geekhub.dto;

import lombok.Getter;
import lombok.Setter;

public class ZipDto {

    @Getter @Setter
    private byte[] zipFile;

    @Getter @Setter
    private String zipName;

    public ZipDto(byte[] zipFile, String zipName) {
        this.zipFile = zipFile;
        this.zipName = zipName;
    }

    public int getContentLength() {
        return zipFile.length;
    }
}
