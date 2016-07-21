package com.geekhub.dto;

import com.geekhub.entities.enums.DocumentAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FileAccessDto {

    @Getter @Setter
    private Long fileId;

    @Getter @Setter
    private DocumentAttribute attribute;

    @Getter @Setter
    private List<UserDto> readers;

    @Getter @Setter
    private List<FriendGroupDto> readerGroups;

    @Getter @Setter
    private List<UserDto> editors;

    @Getter @Setter
    private List<FriendGroupDto> editorGroups;
}
