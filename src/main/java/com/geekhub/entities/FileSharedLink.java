package com.geekhub.entities;

import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.services.enams.FileType;
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

    @Column
    @Getter @Setter
    private Long fileId;

    @Column
    @Getter @Setter
    private FileType fileType;

    @Column
    @Getter @Setter
    private String baseUrl;

    @Column
    @Getter @Setter
    private String hash;

    @Column(name = "relation_type")
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
