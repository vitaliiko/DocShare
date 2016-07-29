package com.geekhub.entities;

import com.geekhub.entities.enums.FileRelationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "file_shared_link")
public class FileSharedLink {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column(nullable = false)
    @Getter @Setter
    private Long fileId;

    @Column(unique = true, nullable = false)
    @Getter @Setter
    private String fileHashName;

    @Column(unique = true, nullable = false)
    @Getter @Setter
    private String hash;

    @Column(name = "relation_type", nullable = false)
    @Enumerated
    @Getter @Setter
    private FileRelationType relationType;

    @Column(name = "last_date")
    @Getter @Setter
    private Date lastDate;

    @Column(name = "max_click_number")
    @Getter @Setter
    private Integer maxClickNumber;

    @Column(name = "click_number")
    @Getter @Setter
    private Integer clickNumber;
}
