package com.geekhub.utils;

import com.geekhub.entities.*;
import com.geekhub.entities.enums.DocumentAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DirectoryWithRelations {

    @Getter
    private UserDirectory directory;

    @Getter @Setter
    private User owner;

    @Getter
    private List<UserToDirectoryRelation> userRelations = new ArrayList<>();

    @Getter
    private List<FriendGroupToDirectoryRelation> groupRelations = new ArrayList<>();

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

    public void setDirectory(UserDirectory directory) {
        this.directory = directory;
        documentAttribute = directory == null ? DocumentAttribute.PRIVATE : directory.getDocumentAttribute();
    }

    public String getHashName() {
        return directory.getHashName();
    }

    public void addRelation(UserToDirectoryRelation relation) {
        userRelations.add(relation);
    }

    public void addAllUserRelations(List<UserToDirectoryRelation> relations) {
        userRelations.addAll(relations);
    }

    public void addRelation(FriendGroupToDirectoryRelation relation) {
        groupRelations.add(relation);
    }

    public void addAllGroupRelations(List<FriendGroupToDirectoryRelation> relations) {
        groupRelations.addAll(relations);
    }

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
