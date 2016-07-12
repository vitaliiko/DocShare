package com.geekhub.entities;


import com.geekhub.entities.enums.FileRelationType;

import javax.persistence.*;

@Entity
@Table(name = "friend_group_to_directory_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"directory_id", "friend_group_id"}))
public class FriendGroupToDirectoryRelation {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "directory_id")
    private UserDirectory directory;

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

    public UserDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(UserDirectory directory) {
        this.directory = directory;
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
