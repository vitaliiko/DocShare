package com.geekhub.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "friends_group")
@ToString(of = "name")
@EqualsAndHashCode(of = "id")
public class FriendsGroup implements Serializable {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @Getter @Setter
    private User owner;

    @Column
    @Getter @Setter
    private String name;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "user_to_group_relation",
            joinColumns = {
                    @JoinColumn(name = "groupId")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "userId")
            }
    )
    @Getter @Setter
    private Set<User> friends = new HashSet<>();

}
