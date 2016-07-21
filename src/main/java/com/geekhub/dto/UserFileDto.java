package com.geekhub.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@EqualsAndHashCode(of = "id")
public class UserFileDto implements Comparable<UserFileDto> {

    @Getter @Setter
    private long id;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String ownerName;

    @Getter @Setter
    private String access;

    @Getter @Setter
    private String size;

    @Getter @Setter
    private Date lastModifyTime;

    @Getter @Setter
    private String modifiedBy;

    @Override
    public int compareTo(UserFileDto o) {
        return this.getName().compareTo(o.getName());
    }
}
