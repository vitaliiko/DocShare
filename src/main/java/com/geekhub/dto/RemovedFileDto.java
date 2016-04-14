package com.geekhub.dto;

import java.util.Date;

public class RemovedFileDto implements Comparable<RemovedFileDto> {

    private long id;
    private String name;
    private String type;
    private Date removalDate;
    private String removerName;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getRemovalDate() {
        return removalDate;
    }

    public void setRemovalDate(Date removalDate) {
        this.removalDate = removalDate;
    }

    public String getRemoverName() {
        return removerName;
    }

    public void setRemoverName(String removerName) {
        this.removerName = removerName;
    }

    @Override
    public int compareTo(RemovedFileDto o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RemovedFileDto that = (RemovedFileDto) o;

        return id == that.id
                && name.equals(that.name)
                && type.equals(that.type)
                && removalDate.equals(that.removalDate)
                && removerName.equals(that.removerName);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + removalDate.hashCode();
        result = 31 * result + removerName.hashCode();
        return result;
    }
}
