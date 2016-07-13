package com.geekhub.entities;

import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_directory")
public class UserDirectory implements Comparable<UserDirectory>, Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private String parentDirectoryHash;

    @Column(unique = true)
    private String hashName;

    @Column(name = "documentAttribute")
    @Enumerated(EnumType.STRING)
    private DocumentAttribute documentAttribute = DocumentAttribute.PRIVATE;

    @Column(name = "documentStatus")
    @Enumerated(EnumType.STRING)
    private DocumentStatus documentStatus = DocumentStatus.ACTUAL;

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

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
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
