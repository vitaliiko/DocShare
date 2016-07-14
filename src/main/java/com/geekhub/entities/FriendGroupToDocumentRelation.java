package com.geekhub.entities;

import com.geekhub.entities.enums.FileRelationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "friend_group_to_document_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "friend_group_id"}))
public class FriendGroupToDocumentRelation {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @OneToOne
    @JoinColumn(name = "document_id")
    @Getter @Setter
    private UserDocument document;

    @OneToOne
    @JoinColumn(name = "friend_group_id")
    @Getter @Setter
    private FriendsGroup friendsGroup;

    @Column(name = "relation")
    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private FileRelationType fileRelationType;


}
