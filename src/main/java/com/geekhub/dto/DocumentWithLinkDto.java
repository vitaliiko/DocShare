package com.geekhub.dto;

import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.entities.enums.FileRelationType;
import lombok.Getter;
import lombok.Setter;

public class DocumentWithLinkDto extends UserFileDto {
    
    @Getter @Setter
    private String token;

    @Getter @Setter
    private FileRelationType relationType;

    @Getter @Setter
    private AbilityToCommentDocument abilityToCommentDocument;
}
