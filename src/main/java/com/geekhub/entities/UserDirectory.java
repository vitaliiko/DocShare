package com.geekhub.entities;

import com.geekhub.entities.enums.DocumentAttribute;
import com.geekhub.entities.enums.DocumentStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
@EqualsAndHashCode(of = "id")
public class UserDirectory implements Comparable<UserDirectory>, Serializable {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column
    @Getter @Setter
    private String name;

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

    @Override
    public int compareTo(UserDirectory o) {
        return this.getName().compareTo(o.getName());
    }
}
