package com.geekhub.dto;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import java.util.Date;
import java.util.Set;

public class UserFileDto implements Comparable<UserFileDto> {

    private long id;
    private String type;
    private String name;
    private String hashName;
    private String access;
    private String size;
    private String parentDirectoryHash;
    private String lastModifyTime;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashName() {
        return hashName;
    }

    public void setHashName(String hashName) {
        this.hashName = hashName;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getParentDirectoryHash() {
        return parentDirectoryHash;
    }

    public void setParentDirectoryHash(String parentDirectoryHash) {
        this.parentDirectoryHash = parentDirectoryHash;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
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

    @Override
    public int compareTo(UserFileDto o) {
        return this.getName().compareTo(o.getName());
    }
}
