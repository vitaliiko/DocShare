package com.geekhub.entities;

import com.geekhub.entities.enums.FileRelationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_to_document_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "user_id"}))
public class UserToDocumentRelation {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @OneToOne
    @JoinColumn(name = "document_id")
    @Getter @Setter
    private UserDocument document;

    @OneToOne
    @JoinColumn(name = "user_id")
    @Getter @Setter
    private User user;

    @Column(name = "relation")
    @Enumerated
    @Getter @Setter
    private FileRelationType fileRelationType;

}
