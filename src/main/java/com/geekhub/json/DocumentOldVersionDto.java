package com.geekhub.json;

import java.util.Date;

public class DocumentOldVersionDto {

    private long id;
    private String name;
    private String description;
    private Date lastModifyTime;
    private String size;

    public DocumentOldVersionDto() {}

    public DocumentOldVersionDto(long id, String name, String description, Date lastModifyDate, String size) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lastModifyTime = lastModifyDate;
        this.size = size;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
