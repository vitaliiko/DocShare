package com.geekhub.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@EqualsAndHashCode
public class DocumentOldVersionDto {

    @Getter @Setter
    private long id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String changedBy;

    @Getter @Setter
    private Date lastModifyTime;

    @Getter @Setter
    private String size;

}
