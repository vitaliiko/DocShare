package com.geekhub.dto;

import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.services.enams.FileType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class FileSharedLinkDto {

    @Getter @Setter
    private String url;

    @Getter @Setter
    private String fileHashName;

    @Getter @Setter
    private FileType fileType;

    @Getter @Setter
    private FileRelationType relationType;

    @Getter @Setter
    private Date lastDate;

    @Getter @Setter
    private int maxClickNumber;

    @Getter @Setter
    private int clickNumber;
}
