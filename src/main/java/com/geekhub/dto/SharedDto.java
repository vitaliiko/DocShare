package com.geekhub.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SharedDto {

    @NotNull
    @Getter @Setter
    private String access;

    @Getter @Setter
    private List<Long> readers;

    @Getter @Setter
    private List<Long> readerGroups;

    @Getter @Setter
    private List<Long> editors;

    @Getter @Setter
    private List<Long> editorGroups;

}
