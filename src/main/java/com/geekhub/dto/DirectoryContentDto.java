package com.geekhub.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.TreeSet;

public class DirectoryContentDto {

    @Getter @Setter
    private String dirHashName;

    @Getter @Setter
    private String parentDirHashName;

    @Getter @Setter
    private Set<UserFileDto> files = new TreeSet<>();

    public void addFile(UserFileDto fileDto) {
        files.add(fileDto);
    }
}
