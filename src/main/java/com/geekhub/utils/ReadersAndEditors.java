package com.geekhub.utils;

import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.enums.DocumentAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ReadersAndEditors {

    @Getter @Setter
    private DocumentAttribute documentAttribute;

    @Getter
    private List<User> readers = new ArrayList<>();

    @Getter
    private List<FriendsGroup> readerGroups = new ArrayList<>();

    @Getter
    private List<User> editors = new ArrayList<>();

    @Getter
    private List<FriendsGroup> editorGroups = new ArrayList<>();

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
