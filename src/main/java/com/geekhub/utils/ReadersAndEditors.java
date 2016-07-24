package com.geekhub.utils;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ReadersAndEditors {

    @Getter
    private List<User> readers;

    @Getter
    private List<FriendsGroup> readerGroups;

    @Getter
    private List<User> editors;

    @Getter
    private List<FriendsGroup> editorGroups;

    public void addAllReaders(List<User> readers) {
        this.readers.addAll(readers);
    }

    public void addAllEditors(List<User> editors) {
        this.editors.addAll(editors);
    }

    public void addAllReaderGroups(List<FriendsGroup> readerGroups) {
        this.readerGroups.addAll(readerGroups);
    }

    public void addAllEditorGroups(List<FriendsGroup> editorGroups) {
        this.editorGroups.addAll(editorGroups);
    }
}
