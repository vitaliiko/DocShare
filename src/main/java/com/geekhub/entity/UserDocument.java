package com.geekhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class UserDocument extends UserFile implements Comparable<UserDocument> {

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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
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
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
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
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
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
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDocument document = (UserDocument) o;

        if (id != null ? !id.equals(document.id) : document.id != null) return false;
        if (type != null ? !type.equals(document.type) : document.type != null) return false;
        if (description != null ? !description.equals(document.description) : document.description != null)
            return false;
        if (lastModifyTime != null ? !lastModifyTime.equals(document.lastModifyTime) : document.lastModifyTime != null)
            return false;
        if (size != null ? !size.equals(document.size) : document.size != null) return false;
        if (owner != null ? !owner.equals(document.owner) : document.owner != null) return false;
        if (readers != null ? !readers.equals(document.readers) : document.readers != null) return false;
        return comments != null ? comments.equals(document.comments) : document.comments == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (lastModifyTime != null ? lastModifyTime.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (readers != null ? readers.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(UserDocument o) {
        return this.getName().compareTo(o.getName());
    }
}
