package com.geekhub.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_document_statistic")
public class UserDocumentStatistic {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column(name = "all_views", columnDefinition = "default 0")
    @Getter @Setter
    private Integer allViews;

    @Column(name = "last_version_views", columnDefinition = "default 0")
    @Getter @Setter
    private Integer lastVersionViews;

    @Column(name = "all_downloads", columnDefinition = "default 0")
    @Getter @Setter
    private Integer allDownloads;

    @Column(name = "last_version_downloads", columnDefinition = "default 0")
    @Getter @Setter
    private Integer lastVersionDownloads;

    @OneToOne
    @JoinColumn(name = "user_document_id")
    @Getter @Setter
    private UserDocument userDocument;

    public void incAllViews() {
        allViews++;
    }

    public void incLastVersionViews() {
        lastVersionViews++;
    }

    public void incAllDownloads() {
        allDownloads++;
    }

    public void incLastVersionDownloads() {
        lastVersionDownloads++;
    }

    public void incAll() {
        allViews++;
        allDownloads++;
        lastVersionViews++;
        lastVersionDownloads++;
    }
}
