package com.geekhub.entities;

import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "user_document")
public class UserDocument implements Comparable<UserDocument>, Serializable {

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
    private String modifiedBy;

    @Column
    private String size;

    @Column
    private String name;

    @Column
    private String extension;

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

    @Column(name = "abilityToComment")
    @Enumerated(EnumType.STRING)
    private AbilityToCommentDocument abilityToComment = AbilityToCommentDocument.ENABLE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "doc_id")
    private Set<Comment> comments = new HashSet<>();

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

    public Set<DocumentOldVersion> getDocumentOldVersions() {
        return documentOldVersions;
    }

    public void setDocumentOldVersions(Set<DocumentOldVersion> documentOldVersions) {
        this.documentOldVersions = documentOldVersions;
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

    public AbilityToCommentDocument getAbilityToComment() {
        return abilityToComment;
    }

    public void setAbilityToComment(AbilityToCommentDocument abilityToComment) {
        this.abilityToComment = abilityToComment;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setNameWithExtension(String name) {
        this.name = name;
        this.extension = name.substring(name.lastIndexOf("."));
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
