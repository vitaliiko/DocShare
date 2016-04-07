package com.geekhub.json;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import java.util.Set;

public class DocumentJson {

    private long id;
    private String name;
    private Set<User> readers;
    private String access;
    private Set<FriendsGroup> readersGroup;

    public DocumentJson(long id, String name, String access, Set<User> readers, Set<FriendsGroup> readersGroup) {
        this.id = id;
        this.name = name;
        this.readers = readers;
        this.access = access;
        this.readersGroup = readersGroup;
    }

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
