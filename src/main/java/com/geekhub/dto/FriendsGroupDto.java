package com.geekhub.dto;

import com.geekhub.entity.User;
import java.util.Set;

public class FriendsGroupDto {

    private long id;
    private String name;
    private Set<User> friends;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }
}
