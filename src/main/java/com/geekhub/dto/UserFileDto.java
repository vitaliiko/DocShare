package com.geekhub.dto;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

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
    private String hashName;

    @Getter @Setter
    private String access;

    @Getter @Setter
    private String size;

    @Getter @Setter
    private String parentDirectoryHash;

    @Getter @Setter
    private Date lastModifyTime;

    @Getter @Setter
    private String modifiedBy;

    @Getter @Setter
    private List<User> readers;

    @Getter @Setter
    private List<FriendsGroup> readerGroups;

    @Getter @Setter
    private List<User> editors;

    @Getter @Setter
    private List<FriendsGroup> editorGroups;

    @Override
    public int compareTo(UserFileDto o) {
        return this.getName().compareTo(o.getName());
    }
}
