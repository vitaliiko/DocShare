package com.geekhub.entities;

import com.geekhub.entities.enums.FileRelationType;
import com.geekhub.services.enams.FileType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(name = "file_hash_name", unique = true, nullable = false)
    @Getter @Setter
    private String fileHashName;

    @Column(name = "file_type")
    @Enumerated
    @Getter @Setter
    private FileType fileType;

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
    @ColumnDefault("0")
    @Getter @Setter
    private Integer clickNumber;

    @Column(nullable = false)
    @Getter @Setter
    private Long userId;
}
