package com.geekhub.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "document_old_version")
public class DocumentOldVersion {

    @Id
    @GeneratedValue
    private Long id;
    
    @Column
    private String name;

    @Column
    private String hashName;
    
    @Column
    private String size;
    
    @Column
    private Date lastModifyDate;

    @Column
    private String changedBy;

    @ManyToOne
    @JoinColumn(name = "userdocument_id")
    private UserDocument userDocument;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDocument getUserDocument() {
        return userDocument;
    }

    public void setUserDocument(UserDocument userDocument) {
        this.userDocument = userDocument;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashName() {
        return hashName;
    }

    public void setHashName(String hashName) {
        this.hashName = hashName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentOldVersion that = (DocumentOldVersion) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (userDocument != null ? !userDocument.equals(that.userDocument) : that.userDocument != null) return false;
        return changedBy != null ? changedBy.equals(that.changedBy) : that.changedBy == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userDocument != null ? userDocument.hashCode() : 0);
        result = 31 * result + (changedBy != null ? changedBy.hashCode() : 0);
        return result;
    }
}
