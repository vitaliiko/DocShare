package com.geekhub.entities;

import com.geekhub.entities.enums.FileRelationType;

import javax.persistence.*;

@Entity
@Table(name = "friend_group_to_document_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "friend_group_id"}))
public class FriendGroupToDocumentRelation {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "document_id")
    private UserDocument document;

    @OneToOne
    @JoinColumn(name = "friend_group_id")
    private FriendsGroup friendsGroup;

    @Column(name = "relation")
    @Enumerated(EnumType.STRING)
    private FileRelationType fileRelationType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDocument getDocument() {
        return document;
    }

    public void setDocument(UserDocument document) {
        this.document = document;
    }

    public FriendsGroup getFriendsGroup() {
        return friendsGroup;
    }

    public void setFriendsGroup(FriendsGroup friendsGroup) {
        this.friendsGroup = friendsGroup;
    }

    public FileRelationType getFileRelationType() {
        return fileRelationType;
    }

    public void setFileRelationType(FileRelationType fileRelationType) {
        this.fileRelationType = fileRelationType;
    }
}
