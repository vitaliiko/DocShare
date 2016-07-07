package com.geekhub.dto;

import java.util.List;

public class SharedDto {

    private long docId;
    private String access;
    private List<Long> readers;
    private List<Long> readersGroups;
    private List<Long> editors;
    private List<Long> editorsGroups;

    public long getDocId() {
        return docId;
    }

    public void setDocId(long docId) {
        this.docId = docId;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public List<Long> getReaders() {
        return readers;
    }

    public void setReaders(List<Long> readers) {
        this.readers = readers;
    }

    public List<Long> getReadersGroups() {
        return readersGroups;
    }

    public void setReadersGroups(List<Long> readersGroups) {
        this.readersGroups = readersGroups;
    }

    public List<Long> getEditors() {
        return editors;
    }

    public void setEditors(List<Long> editors) {
        this.editors = editors;
    }

    public List<Long> getEditorsGroups() {
        return editorsGroups;
    }

    public void setEditorsGroups(List<Long> editorsGroups) {
        this.editorsGroups = editorsGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SharedDto sharedDto = (SharedDto) o;

        return docId == sharedDto.docId && access.equals(sharedDto.access);

    }

    @Override
    public int hashCode() {
        int result = (int) (docId ^ (docId >>> 32));
        result = 31 * result + access.hashCode();
        return result;
    }
}
