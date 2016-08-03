package com.geekhub.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
@ToString(exclude = "text")
@EqualsAndHashCode(of = "id")
public class Comment implements Serializable {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column(nullable = false, length = 1024)
    @Getter @Setter
    private String text;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @Getter @Setter
    private User owner;

    @Column(nullable = false, columnDefinition = "DATETIME")
    @Getter @Setter
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "doc_id")
    @Getter @Setter
    private UserDocument userDocument;

}
