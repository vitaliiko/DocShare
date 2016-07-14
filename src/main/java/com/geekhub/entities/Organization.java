package com.geekhub.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
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
@ToString(of = "name")
@EqualsAndHashCode(of = "id")
public class Organization implements Serializable {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column
    @Getter @Setter
    private String name;

    @Column
    @Lob @Basic
    @Getter @Setter
    private byte[] avatar;

    @Column
    @Getter @Setter
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @Getter @Setter
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
    @Getter @Setter
    private Set<User> members = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    @Getter @Setter
    private Set<UserDocument> userDocuments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    @Getter @Setter
    private Set<UserDirectory> userDirectories = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    @Getter @Setter
    private Set<RemovedDocument> removedDocuments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    @Getter @Setter
    private Set<RemovedDirectory> removedDirectories = new HashSet<>();

}
