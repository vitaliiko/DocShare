package com.geekhub.entities;

import com.geekhub.entities.enums.FileRelationType;

import javax.persistence.*;

@Entity
@Table(name = "user_to_directory_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"directory_id", "user_id"}))
public class UserToDirectoryRelation {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "directory_id")
    private UserDirectory directory;

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

    public UserDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(UserDirectory directory) {
        this.directory = directory;
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
