package com.geekhub.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class Organization {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Lob
    @Basic
    @Column
    private byte[] avatar;

    @Column
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "user_to_organization_relation",
            joinColumns = {
                    @JoinColumn(name = "organization_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "member_id")
            }
    )
    private Set<User> members = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Set<UserDocument> userDocuments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Set<UserDirectory> userDirectories = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Set<RemovedDocument> removedDocuments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Set<RemovedDirectory> removedDirectories = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public Set<UserDocument> getUserDocuments() {
        return userDocuments;
    }

    public void setUserDocuments(Set<UserDocument> userDocuments) {
        this.userDocuments = userDocuments;
    }

    public Set<UserDirectory> getUserDirectories() {
        return userDirectories;
    }

    public void setUserDirectories(Set<UserDirectory> userDirectories) {
        this.userDirectories = userDirectories;
    }

    public Set<RemovedDocument> getRemovedDocuments() {
        return removedDocuments;
    }

    public void setRemovedDocuments(Set<RemovedDocument> removedDocuments) {
        this.removedDocuments = removedDocuments;
    }

    public Set<RemovedDirectory> getRemovedDirectories() {
        return removedDirectories;
    }

    public void setRemovedDirectories(Set<RemovedDirectory> removedDirectories) {
        this.removedDirectories = removedDirectories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organization that = (Organization) o;

        return id.equals(that.id)
                && name.equals(that.name)
                && creationDate.equals(that.creationDate)
                && creator.equals(that.creator);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + creationDate.hashCode();
        result = 31 * result + creator.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
