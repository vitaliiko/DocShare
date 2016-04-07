package com.geekhub.json;

public class SharedJson {

    private long docId;
    private String access;
    private long[] readers;
    private long[] readersGroups;

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
}
