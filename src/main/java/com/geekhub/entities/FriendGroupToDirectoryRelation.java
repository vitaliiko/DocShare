package com.geekhub.entities;


import com.geekhub.entities.enums.FileRelationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "friend_group_to_directory_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"directory_id", "friend_group_id"}))
public class FriendGroupToDirectoryRelation {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @OneToOne
    @JoinColumn(name = "directory_id")
    @Getter @Setter
    private UserDirectory directory;

    @OneToOne
    @JoinColumn(name = "friend_group_id")
    @Getter @Setter
    private FriendsGroup friendsGroup;

    @Column(name = "relation")
    @Enumerated
    @Getter @Setter
    private FileRelationType fileRelationType;

}
