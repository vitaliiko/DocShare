package com.geekhub.entities;

import com.geekhub.entities.enums.AbilityToCommentDocument;
import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
@EqualsAndHashCode(of = "id")
public class UserDocument implements Comparable<UserDocument>, Serializable {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column
    @Getter @Setter
    private String type;

    @Column
    @Getter @Setter
    private String description;

    @Column
    @Getter @Setter
    private Date lastModifyTime;

    @Column
    @Getter @Setter
    private String modifiedBy;

    @Column
    @Getter @Setter
    private String size;

    @Column
    @Getter @Setter
    private String name;

    @Column
    @Getter @Setter
    private String extension;

    @Column
    @Getter @Setter
    private String parentDirectoryHash;

    @Column(unique = true)
    @Getter @Setter
    private String hashName;

    @Column(name = "documentAttribute")
    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private DocumentAttribute documentAttribute = DocumentAttribute.PRIVATE;

    @Column(name = "documentStatus")
    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private DocumentStatus documentStatus = DocumentStatus.ACTUAL;

    @Column(name = "abilityToComment")
    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private AbilityToCommentDocument abilityToComment = AbilityToCommentDocument.ENABLE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "doc_id")
    @Getter @Setter
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "userdocument_id")
    @Getter @Setter
    private Set<DocumentOldVersion> documentOldVersions = new HashSet<>();

    public void setNameWithExtension(String name) {
        this.name = name;
        this.extension = name.substring(name.lastIndexOf("."));
    }

    @Override
    public int compareTo(UserDocument o) {
        return this.getName().compareTo(o.getName());
    }
}
