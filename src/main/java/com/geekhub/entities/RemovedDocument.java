package com.geekhub.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "removed_document")
@EqualsAndHashCode(of = "id")
public class RemovedDocument {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @Getter @Setter
    private User owner;

    @OneToOne
    @JoinColumn(name = "document_id")
    @Getter @Setter
    private UserDocument userDocument;

    @Column
    @Getter @Setter
    private Long removerId;

    @Column
    @Getter @Setter
    private LocalDateTime removalDate;

    @Column
    @Getter @Setter
    private String description;

}
