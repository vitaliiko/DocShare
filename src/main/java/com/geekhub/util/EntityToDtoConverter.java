package com.geekhub.util;

import com.geekhub.entity.DocumentOldVersion;
import com.geekhub.json.DocumentOldVersionDto;

public class EntityToDtoConverter {

    public static DocumentOldVersionDto convert(DocumentOldVersion oldVersion) {
        DocumentOldVersionDto oldVersionDto = new DocumentOldVersionDto();
        oldVersionDto.setId(oldVersion.getId());
        oldVersionDto.setName(oldVersion.getUserDocument().getName());
        oldVersionDto.setDescription(oldVersion.getDescription());
        oldVersionDto.setLastModifyTime(oldVersion.getUserDocument().getLastModifyTime());
        oldVersionDto.setSize(oldVersion.getUserDocument().getSize());
        return oldVersionDto;
    }
}
