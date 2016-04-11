package com.geekhub.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geekhub.entities.enums.DocumentAttribute;
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
import javax.persistence.Table;

@Entity
@Table(name = "user_directory")
public class UserDirectory implements Comparable<UserDirectory> {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

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
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "reader_to_directory_relation",
            joinColumns = {
                    @JoinColumn(name = "userdirectory_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "reader_id")
            }
    )
    private Set<User> readers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "readers_group_to_directory_relation",
            joinColumns = {
                    @JoinColumn(name = "userdirectory_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "readersgroup_id")
            }
    )
    private Set<FriendsGroup> readersGroups = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<FriendsGroup> getReadersGroups() {
        return readersGroups;
    }

    public void setReadersGroups(Set<FriendsGroup> readersGroups) {
        this.readersGroups = readersGroups;
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

        UserDirectory directory = (UserDirectory) o;

        return id.equals(directory.id)
                && name.equals(directory.name)
                && parentDirectoryHash.equals(directory.parentDirectoryHash)
                && hashName.equals(directory.hashName);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + parentDirectoryHash.hashCode();
        result = 31 * result + hashName.hashCode();
        return result;
    }

    @Override
    public int compareTo(UserDirectory o) {
        return this.getName().compareTo(o.getName());
    }
}
