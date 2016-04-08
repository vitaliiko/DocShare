package com.geekhub.dto;

import com.geekhub.entity.FriendsGroup;
import com.geekhub.entity.User;
import com.geekhub.entity.enums.DocumentAttribute;
import java.util.Map;
import java.util.Set;

public class UserDirectoryDto implements Comparable<UserDirectoryDto> {

    private long id;
    private String name;
    private Map<Long, String> readers;
    private Map<Long, String> readersGroup;
    private DocumentAttribute documentAttribute;

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

    public Map<Long, String> getReaders() {
        return readers;
    }

    public void setReaders(Map<Long, String> readers) {
        this.readers = readers;
    }

    public Map<Long, String> getReadersGroup() {
        return readersGroup;
    }

    public void setReadersGroup(Map<Long, String> readersGroup) {
        this.readersGroup = readersGroup;
    }

    public DocumentAttribute getDocumentAttribute() {
        return documentAttribute;
    }

    public void setDocumentAttribute(DocumentAttribute documentAttribute) {
        this.documentAttribute = documentAttribute;
    }

    @Override
    public int compareTo(UserDirectoryDto o) {
        return this.getName().compareTo(o.getName());
    }
}
