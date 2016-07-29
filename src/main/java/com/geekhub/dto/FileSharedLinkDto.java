package com.geekhub.dto;

import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.services.enams.FileType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

    @Future
    @Getter @Setter
    private Date lastDate;

    @Min(0) @Max(1024)
    @Getter @Setter
    private Integer maxClickNumber;

    @Getter @Setter
    private Integer clickNumber;
}
