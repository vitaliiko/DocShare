package com.geekhub.dto;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import java.util.Set;

public class UserDocumentDto {

    private long id;
    private String name;
    private String access;
    private Set<User> readers;
    private Set<FriendsGroup> readersGroup;

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

    public Set<User> getReaders() {
        return readers;
    }

    public void setReaders(Set<User> readers) {
        this.readers = readers;
    }

    public Set<FriendsGroup> getReadersGroup() {
        return readersGroup;
    }

    public void setReadersGroup(Set<FriendsGroup> readersGroup) {
        this.readersGroup = readersGroup;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }
}
