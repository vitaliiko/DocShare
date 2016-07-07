package com.geekhub.entities;

import javax.persistence.*;

@Entity
@Table(name = "document_stat")
public class DocumentStatistic {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Integer allViews;

    @Column
    private Integer lastVersionViews;

    @Column
    private Integer allDownloads;

    @Column
    private Integer lastVersionDownloads;

    @OneToOne
    @JoinColumn(name = "user_document_id")
    private UserDocument userDocument;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAllViews() {
        return allViews;
    }

    public void setAllViews(Integer allViews) {
        this.allViews = allViews;
    }

    public Integer getLastVersionViews() {
        return lastVersionViews;
    }

    public void setLastVersionViews(Integer lastVersionViews) {
        this.lastVersionViews = lastVersionViews;
    }

    public Integer getAllDownloads() {
        return allDownloads;
    }

    public void setAllDownloads(Integer allDownloads) {
        this.allDownloads = allDownloads;
    }

    public Integer getLastVersionDownloads() {
        return lastVersionDownloads;
    }

    public void setLastVersionDownloads(Integer lastVersionDownloads) {
        this.lastVersionDownloads = lastVersionDownloads;
    }

    public UserDocument getUserDocument() {
        return userDocument;
    }

    public void setUserDocument(UserDocument userDocument) {
        this.userDocument = userDocument;
    }
}
