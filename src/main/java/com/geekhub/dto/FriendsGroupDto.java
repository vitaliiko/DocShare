package com.geekhub.dto;

import com.geekhub.entity.User;
import java.util.Set;

public class FriendsGroupDto implements Comparable<FriendsGroupDto> {

    private long id;
    private String name;
    private Set<UserDto> friends;

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

    public Set<UserDto> getFriends() {
        return friends;
    }

    public void setFriends(Set<UserDto> friends) {
        this.friends = friends;
    }

    @Override
    public int compareTo(FriendsGroupDto o) {
        return this.getName().compareTo(o.getName());
    }
}
