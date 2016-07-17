package com.geekhub.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SharedDto {

    @Getter @Setter
    private String access;

    @Getter @Setter
    private List<Long> readers;

    @Getter @Setter
    private List<Long> readersGroups;

    @Getter @Setter
    private List<Long> editors;

    @Getter @Setter
    private List<Long> editorsGroups;

}
