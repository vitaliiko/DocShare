package com.geekhub.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "removed_directory")
@EqualsAndHashCode(of = "id")
public class RemovedDirectory {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @Getter @Setter
    private User owner;

    @OneToOne
    @JoinColumn(name = "directory_id")
    @Getter @Setter
    private UserDirectory userDirectory;

    @Column
    @Getter @Setter
    private Long removerId;

    @Column
    @Getter @Setter
    private Date removalDate;

    @Column
    @Getter @Setter
    private String description;

}
