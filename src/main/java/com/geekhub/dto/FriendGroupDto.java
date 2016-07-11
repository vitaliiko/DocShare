package com.geekhub.dto;

import java.util.Set;

public class FriendGroupDto implements Comparable<FriendGroupDto> {

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
    public int compareTo(FriendGroupDto o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendGroupDto groupDto = (FriendGroupDto) o;

        return id == groupDto.id
                && name.equals(groupDto.name)
                && friends.equals(groupDto.friends);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + friends.hashCode();
        return result;
    }
}
