package com.geekhub.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

public class FileIdsDto {

    @Getter @Setter
    private List<Long> docIds;

    @Getter @Setter
    private List<Long> dirIds;

    @NotNull
    @Getter @Setter
    private String destinationDirHash;

}
