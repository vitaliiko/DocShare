package com.geekhub.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geekhub.entities.enums.DocumentAttribute;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "user_document")
public class UserDocument implements Comparable<UserDocument> {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String type;

    @Column
    private String description;

    @Column
    private Date lastModifyTime;

    @Column
    private String size;

    @Column
    private String name;

    @Column
    private String parentDirectoryHash;

    @Column
    private String hashName;

    @Column(name = "documentAttribute")
    @Enumerated(EnumType.STRING)
    private DocumentAttribute documentAttribute;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "reader_to_document_relation",
            joinColumns = {
                    @JoinColumn(name = "userdocument_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "reader_id")
            }
    )
    private Set<User> readers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "editor_to_document_relation",
            joinColumns = {
                    @JoinColumn(name = "userdocument_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "editor_id")
            }
    )
    private Set<User> editors = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "readers_group_to_document_relation",
            joinColumns = {
                    @JoinColumn(name = "userdocument_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "readersgroup_id")
            }
    )
    private Set<FriendsGroup> readersGroups = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "editors_group_to_document_relation",
            joinColumns = {
                    @JoinColumn(name = "userdocument_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "editorsgroup_id")
            }
    )
    private Set<FriendsGroup> editorsGroups = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "doc_id")
    private Set<Comment> comments = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "userdocument_id")
    private Set<DocumentOldVersion> documentOldVersions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getReaders() {
        return readers;
    }

    public void setReaders(Set<User> readers) {
        this.readers = readers;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<FriendsGroup> getReadersGroups() {
        return readersGroups;
    }

    public void setReadersGroups(Set<FriendsGroup> readersGroups) {
        this.readersGroups = readersGroups;
    }

    public Set<DocumentOldVersion> getDocumentOldVersions() {
        return documentOldVersions;
    }

    public void setDocumentOldVersions(Set<DocumentOldVersion> documentOldVersions) {
        this.documentOldVersions = documentOldVersions;
    }

    public Set<User> getEditors() {
        return editors;
    }

    public void setEditors(Set<User> editors) {
        this.editors = editors;
    }

    public Set<FriendsGroup> getEditorsGroups() {
        return editorsGroups;
    }

    public void setEditorsGroups(Set<FriendsGroup> editorsGroups) {
        this.editorsGroups = editorsGroups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentDirectoryHash() {
        return parentDirectoryHash;
    }

    public void setParentDirectoryHash(String parentDirectoryHash) {
        this.parentDirectoryHash = parentDirectoryHash;
    }

    public String getHashName() {
        return hashName;
    }

    public void setHashName(String hashName) {
        this.hashName = hashName;
    }

    public DocumentAttribute getDocumentAttribute() {
        return documentAttribute;
    }

    public void setDocumentAttribute(DocumentAttribute documentAttribute) {
        this.documentAttribute = documentAttribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDocument document = (UserDocument) o;

        return id.equals(document.id)
                && type.equals(document.type)
                && lastModifyTime.equals(document.lastModifyTime)
                && name.equals(document.name)
                && parentDirectoryHash.equals(document.parentDirectoryHash)
                && hashName.equals(document.hashName);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + lastModifyTime.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + parentDirectoryHash.hashCode();
        result = 31 * result + hashName.hashCode();
        return result;
    }

    @Override
    public int compareTo(UserDocument o) {
        return this.getName().compareTo(o.getName());
    }
}
