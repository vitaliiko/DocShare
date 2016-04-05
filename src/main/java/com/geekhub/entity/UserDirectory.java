package com.geekhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geekhub.enums.DocumentAttribute;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class UserDirectory {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private String location;

    @Column
    private String hashName;

    @Column
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "documentAttribute")
    @Enumerated(EnumType.STRING)
    private DocumentAttribute documentAttribute;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "userToDocumentRelation",
            joinColumns = {
                    @JoinColumn(name = "userdocument_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "reader_id")
            }
    )
    private Set<User> readers = new HashSet<>();

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHashName() {
        return hashName;
    }

    public void setHashName(String hashName) {
        this.hashName = hashName;
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

    public DocumentAttribute getDocumentAttribute() {
        return documentAttribute;
    }

    public void setDocumentAttribute(DocumentAttribute documentAttribute) {
        this.documentAttribute = documentAttribute;
    }

    public Set<User> getReaders() {
        return readers;
    }

    public void setReaders(Set<User> readers) {
        this.readers = readers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDirectory that = (UserDirectory) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (hashName != null ? !hashName.equals(that.hashName) : that.hashName != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (documentAttribute != that.documentAttribute) return false;
        return readers != null ? readers.equals(that.readers) : that.readers == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (hashName != null ? hashName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (documentAttribute != null ? documentAttribute.hashCode() : 0);
        result = 31 * result + (readers != null ? readers.hashCode() : 0);
        return result;
    }
}
