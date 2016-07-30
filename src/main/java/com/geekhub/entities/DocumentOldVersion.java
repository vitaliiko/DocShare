package com.geekhub.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
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
@EqualsAndHashCode(of = "id")
public class DocumentOldVersion implements Serializable {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;
    
    @Column
    @Getter @Setter
    private String name;

    @Column
    @Getter @Setter
    private String hashName;
    
    @Column
    @Getter @Setter
    private String size;
    
    @Column
    @Getter @Setter
    private Date lastModifyTime;

    @Column
    @Getter @Setter
    private String modifierName;

    @Column
    @Getter @Setter
    private Long modifierId;

    @ManyToOne
    @JoinColumn(name = "userdocument_id")
    @Getter @Setter
    private UserDocument userDocument;

}
