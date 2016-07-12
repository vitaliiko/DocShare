package com.geekhub.entities;

import com.geekhub.entities.enums.FileRelationType;

import javax.persistence.*;

@Entity
@Table(name = "user_to_document_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "user_id"}))
public class UserToDocumentRelation {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "document_id")
    private UserDocument document;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FileRelationType getFileRelationType() {
        return fileRelationType;
    }

    public void setFileRelationType(FileRelationType fileRelationType) {
        this.fileRelationType = fileRelationType;
    }
}
