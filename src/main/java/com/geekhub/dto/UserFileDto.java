package com.geekhub.dto;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import java.util.Set;

public class UserFileDto {

    private long id;
    private String name;
    private String access;
    private Set<User> readers;
    private Set<FriendsGroup> readersGroups;
    private Set<User> editors;
    private Set<FriendsGroup> editorsGroups;

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

    public Set<FriendsGroup> getReadersGroups() {
        return readersGroups;
    }

    public void setReadersGroups(Set<FriendsGroup> readersGroups) {
        this.readersGroups = readersGroups;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public Set<User> getEditors() {
        return editors;
    }

    public void setEditors(Set<User> editors) {
        this.editors = editors;
    }

    public Set<FriendsGroup> getEditorsGroups() {
        return editorsGroups;
    }

    public void setEditorsGroups(Set<FriendsGroup> editorsGroups) {
        this.editorsGroups = editorsGroups;
    }
}
