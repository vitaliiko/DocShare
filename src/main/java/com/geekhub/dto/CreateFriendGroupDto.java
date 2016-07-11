package com.geekhub.dto;

import javax.validation.constraints.NotNull;

public class CreateFriendGroupDto {

    private long id;

    @NotNull
    private String groupName;

    private Long[] friends;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long[] getFriends() {
        return friends;
    }

    public void setFriends(Long[] friends) {
        this.friends = friends;
    }
}
