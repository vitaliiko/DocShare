package com.geekhub.entities;

import com.geekhub.entities.enums.FileRelationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_to_directory_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"directory_id", "user_id"}))
public class UserToDirectoryRelation {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @OneToOne
    @JoinColumn(name = "directory_id")
    @Getter @Setter
    private UserDirectory directory;

    @OneToOne
    @JoinColumn(name = "user_id")
    @Getter @Setter
    private User user;

    @Column(name = "relation")
    @Enumerated
    @Getter @Setter
    private FileRelationType fileRelationType;

}
