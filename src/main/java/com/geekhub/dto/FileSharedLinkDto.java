package com.geekhub.dto;

import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.services.enams.FileType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class FileSharedLinkDto {

    @NotNull
    @Getter @Setter
    private Long fileId;

    @Getter @Setter
    private String url;

    @NotNull
    @Getter @Setter
    private FileType fileType;

    @Getter @Setter
    private FileRelationType relationType;

    @Getter @Setter
    private Date lastDate;

    @Getter @Setter
    private Integer maxClickNumber;

    @Getter @Setter
    private Integer clickNumber;
}
