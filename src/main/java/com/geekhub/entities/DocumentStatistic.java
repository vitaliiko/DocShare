package com.geekhub.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "document_stat")
public class DocumentStatistic {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column
    @Getter @Setter
    private Integer allViews;

    @Column
    @Getter @Setter
    private Integer lastVersionViews;

    @Column
    @Getter @Setter
    private Integer allDownloads;

    @Column
    @Getter @Setter
    private Integer lastVersionDownloads;

    @OneToOne
    @JoinColumn(name = "user_document_id")
    @Getter @Setter
    private UserDocument userDocument;

}
