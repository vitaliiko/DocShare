package com.geekhub.dto;

import java.util.Date;

public class DocumentOldVersionDto {

    private long id;
    private String name;
    private String changedBy;
    private Date lastModifyTime;
    private String size;

    public DocumentOldVersionDto() {}

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

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentOldVersionDto that = (DocumentOldVersionDto) o;

        return id == that.id && name.equals(that.name)
                && changedBy.equals(that.changedBy)
                && lastModifyTime.equals(that.lastModifyTime)
                && size.equals(that.size);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + changedBy.hashCode();
        result = 31 * result + lastModifyTime.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }
}
