package com.geekhub.dto;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

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
    private String description;

    @Getter @Setter
    private String hashName;

    @Getter @Setter
    private String access;

    @Getter @Setter
    private String size;

    @Getter @Setter
    private String parentDirectoryHash;

    @Getter @Setter
    private String lastModifyTime;

    @Getter @Setter
    private Set<User> readers;

    @Getter @Setter
    private Set<FriendsGroup> readersGroups;

    @Getter @Setter
    private Set<User> editors;

    @Getter @Setter
    private Set<FriendsGroup> editorsGroups;

    @Override
    public int compareTo(UserFileDto o) {
        return this.getName().compareTo(o.getName());
    }
}
