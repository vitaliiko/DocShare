package com.geekhub.dto;

public class SharedDto {

    private long docId;
    private String access;
    private long[] readers;
    private long[] readersGroups;
    private long[] editors;
    private long[] editorsGroups;

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

    public long[] getReaders() {
        return readers;
    }

    public void setReaders(long[] readers) {
        this.readers = readers;
    }

    public long[] getReadersGroups() {
        return readersGroups;
    }

    public void setReadersGroups(long[] readersGroups) {
        this.readersGroups = readersGroups;
    }

    public long[] getEditors() {
        return editors;
    }

    public void setEditors(long[] editors) {
        this.editors = editors;
    }

    public long[] getEditorsGroups() {
        return editorsGroups;
    }

    public void setEditorsGroups(long[] editorsGroups) {
        this.editorsGroups = editorsGroups;
    }
}