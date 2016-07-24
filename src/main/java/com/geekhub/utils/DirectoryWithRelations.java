package com.geekhub.utils;

import com.geekhub.entities.*;
import com.geekhub.entities.enums.DocumentAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DirectoryWithRelations {

    @Getter @Setter
    private UserDirectory directory;

    @Getter @Setter
    private User owner;

    @Getter
    private List<UserToDirectoryRelation> userRelations = new ArrayList<>();

    @Getter
    private List<FriendGroupToDirectoryRelation> groupRelations = new ArrayList<>();

    public DocumentAttribute getDocumentAttribute() {
        return directory.getDocumentAttribute();
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
}
