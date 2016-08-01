package com.geekhub.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "file_shared_link_token")
public class FileSharedLinkToken {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column(nullable = false, unique = true)
    @Getter @Setter
    private String token;

    @Column(name = "creation_date", nullable = false)
    @Getter @Setter
    private Date creationDate;

    @OneToOne
    @Getter @Setter
    private FileSharedLink fileSharedLink;
}
